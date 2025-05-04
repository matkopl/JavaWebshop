package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.*;
import hr.algebra.webshop.model.PaymentMethod;
import hr.algebra.webshop.model.User;
import hr.algebra.webshop.service.CartService;
import hr.algebra.webshop.service.OrderService;
import hr.algebra.webshop.service.ProductService;
import hr.algebra.webshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String viewCart(Model model) {
        List<CartItemWithProductDto> cartItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (CartItemDto cartItem : cartService.getCartItems()) {
            ProductDto product = productService.getProductById(cartItem.getProductId());
            double price = product.getPrice() * cartItem.getQuantity();

            cartItems.add(new CartItemWithProductDto(
                    cartItem.getProductId(),
                    product,
                    cartItem.getQuantity(),
                    price
            ));

            totalPrice += price;
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", totalPrice);

        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") Integer quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping(value = "/update", produces = "application/json")
    @ResponseBody
    public Map<String, Object> updateQuantity(@RequestParam Long productId, @RequestParam Integer quantity) {
        List<CartItemDto> initialCart = cartService.getCartItems();
        boolean existedBefore = initialCart.stream().anyMatch(item -> item.getProductId().equals(productId));

        cartService.updateQuantity(productId, quantity);

        List<CartItemDto> updatedCart = cartService.getCartItems();
        boolean existsAfter = updatedCart.stream().anyMatch(item -> item.getProductId().equals(productId));
        boolean itemRemoved = existedBefore && !existsAfter;

        List<CartItemWithProductDto> cartItems = new ArrayList<>();
        double totalPrice = 0.0;
        for (CartItemDto cartItem : updatedCart) {
            ProductDto product = productService.getProductById(cartItem.getProductId());
            double price = product.getPrice() * cartItem.getQuantity();
            cartItems.add(new CartItemWithProductDto(cartItem.getProductId(), product, cartItem.getQuantity(), price));
            totalPrice += price;
        }

        double subTotal = cartItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .map(CartItemWithProductDto::getSubtotal)
                .orElse(0.0);

        Map<String, Object> response = new HashMap<>();
        response.put("subtotal", subTotal);
        response.put("total", totalPrice);
        response.put("itemRemoved", itemRemoved);
        return response;
    }



    @PostMapping("/remove")
    @ResponseBody
    public Map<String, Object> removeFromCart(@RequestParam Long productId) {
        cartService.removeFromCart(productId);

        List<CartItemDto> cartItems = cartService.getCartItems();
        int cartSize = cartItems.size();

        double total = 0.0;
        for (CartItemDto cartItem : cartItems) {
            ProductDto product = productService.getProductById(cartItem.getProductId());
            total += product.getPrice() * cartItem.getQuantity();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("cartSize", cartSize);
        response.put("total", total);
        return response;
    }


    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@Valid PlaceOrderDto placeOrderDto, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            if (cartService.getCartItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Košarica je prazna");
                return "redirect:/cart";
            }

            User user = userService.findByUsername(principal.getName());

            placeOrderDto.setCartItems(cartService.getCartItems());

            if (placeOrderDto.getPaymentMethod() == PaymentMethod.PAYPAL) {
                redirectAttributes.addFlashAttribute("orderId", orderService.createOrder(user, placeOrderDto).getId());
                return "redirect:/payment/create";
            } else {
                OrderDto order = orderService.createOrder(user, placeOrderDto);

                cartService.clearCart();

                redirectAttributes.addFlashAttribute("success", "Narudžba uspješno izvršena! Plaćanje: Gotovina-pouzeće");
                return "redirect:/orders/" + order.getId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Došlo je do greške: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
    }

    @GetMapping("/size")
    public ResponseEntity<Integer> getCartSize() {
        return ResponseEntity.ok(cartService.getCartItems().size());
    }
}
