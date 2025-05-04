package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.CartItemDto;
import hr.algebra.webshop.dto.CartItemWithProductDto;
import hr.algebra.webshop.dto.ProductDto;
import hr.algebra.webshop.service.CartService;
import hr.algebra.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> removeFromCart(@RequestParam Long productId) {
       cartService.removeFromCart(productId);
       return ResponseEntity.ok("Item removed from cart");
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    @GetMapping("/size")
    public ResponseEntity<Integer> getCartSize() {
        return ResponseEntity.ok(cartService.getCartItems().size());
    }
}
