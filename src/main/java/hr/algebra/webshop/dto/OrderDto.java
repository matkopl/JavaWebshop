package hr.algebra.webshop.dto;

import hr.algebra.webshop.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private LocalDateTime orderDate;
    private PaymentMethod paymentMethod;
    private String userEmail;
    private double totalAmount;
    private List<OrderItemDto> items;
}
