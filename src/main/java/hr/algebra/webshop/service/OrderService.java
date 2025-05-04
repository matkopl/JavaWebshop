package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.*;
import hr.algebra.webshop.exception.EntityNotFoundException;
import hr.algebra.webshop.model.*;
import hr.algebra.webshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;

    public OrderDto createOrder(User user, PlaceOrderDto placeOrderDto) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(now());
        order.setPaymentMethod(placeOrderDto.getPaymentMethod());

        List<CartItemDto> cartItems = cartService.getCartItems();

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create order with empty cart");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (CartItemDto cartItem : cartItems) {
            ProductDto product = productService.getProductById(cartItem.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(productService.getProductEntity(cartItem.getProductId()));
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            orderItems.add(orderItem);
            totalAmount += product.getPrice() * cartItem.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Payment payment = new Payment();
        payment.setStatus(placeOrderDto.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY ?
                PaymentStatus.PENDING : PaymentStatus.COMPLETED);
        payment.setOrder(order);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart();

        return toDto(savedOrder);
    }


    public List<OrderDto> getOrdersForUser(String username) {
        return orderRepository.findByUser_Username(username)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public OrderDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Order with id: " + id + " not found"));
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Order createOrderFromCart(String username, PaymentMethod paymentMethod) { // Return Order entity instead of DTO
        User user = userService.findByUsername(username);

        PlaceOrderDto placeOrderDto = new PlaceOrderDto();
        placeOrderDto.setPaymentMethod(paymentMethod);
        placeOrderDto.setCartItems(cartService.getCartItems());

        return orderRepository.save(createOrderEntity(user, placeOrderDto)); // Save and return the entity
    }

    private Order createOrderEntity(User user, PlaceOrderDto placeOrderDto) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentMethod(placeOrderDto.getPaymentMethod());

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (CartItemDto cartItem : placeOrderDto.getCartItems()) {
            Product product = productService.getProductEntity(cartItem.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            orderItems.add(orderItem);
            totalAmount += product.getPrice() * cartItem.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        return order;
    }

    public double calculateCartTotal() {
        return cartService.getCartItems()
                .stream()
                .mapToDouble(item ->
                        productService.getProductById(item.getProductId()).getPrice() * item.getQuantity()).sum();
    }

    private OrderDto toDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase(),
                        item.getProduct().getImageUrl()))
                .toList();

        PaymentDetailsDto paymentDetails = null;
        if (order.getPayment() != null) {
            paymentDetails = new PaymentDetailsDto(
                    order.getPayment().getPaymentId(),
                    order.getPayment().getPayerId(),
                    order.getPayment().getStatus()
            );
        }

        return new OrderDto(
                order.getId(),
                order.getOrderDate(),
                order.getPaymentMethod(),
                order.getUser().getUsername(),
                order.getTotalAmount(),
                itemDtos,
                paymentDetails
        );
    }
}

