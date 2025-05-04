package hr.algebra.webshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemWithProductDto {
    private Long productId;
    private ProductDto product;
    private Integer quantity;
    private Double subtotal;
}
