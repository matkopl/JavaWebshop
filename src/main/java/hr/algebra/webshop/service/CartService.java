package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.CartItemDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductService productService;
    private final HttpSession session;

    private static final String CART_SESSION_KEY = "cart";

    @SuppressWarnings("unchecked")
    public List<CartItemDto> getCartItems() {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute(CART_SESSION_KEY);
        return cart != null ? cart : new ArrayList<>();
    }

    public void addToCart(Long productId, Integer quantity) {
        productService.getProductById(productId);

        List<CartItemDto> cart = getCartItems();

        cart.stream()
                .filter(cartItemDto -> cartItemDto.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        cartItemDto -> cartItemDto.setQuantity(cartItemDto.getQuantity() + quantity),
                        () -> cart.add(new CartItemDto(productId, quantity))
                );

        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void updateQuantity(Long productId, Integer quantity) {
        List<CartItemDto> cart = getCartItems();

        cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    if(quantity <= 0) {
                        cart.removeIf(i -> i.getProductId().equals(productId));
                    } else {
                        item.setQuantity(quantity);
                    }
                });

        session.setAttribute(CART_SESSION_KEY, new ArrayList<>(cart));
    }


    public void removeFromCart(Long productId) {
        List<CartItemDto> cart = new ArrayList<>(getCartItems());
        cart.removeIf(item -> item.getProductId().equals(productId));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart() {
        session.removeAttribute(CART_SESSION_KEY);
    }
}
