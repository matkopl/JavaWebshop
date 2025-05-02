package hr.algebra.webshop.dto;

import hr.algebra.webshop.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderDto {
    private PaymentMethod paymentMethod;
    private List<CartItemDto> cartItems;
}
