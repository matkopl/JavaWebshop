package hr.algebra.webshop.dto;

import hr.algebra.webshop.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsDto {
    private String paymentId;
    private String payerId;
    private PaymentStatus status;
}