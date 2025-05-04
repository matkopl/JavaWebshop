package hr.algebra.webshop.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentMethod {
    CASH_ON_DELIVERY("Gotovina - pouzeće"),
    PAYPAL("PayPal");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator
    public static PaymentMethod fromString(String value) {
        return Arrays.stream(values())
                .filter(method -> method.name().equalsIgnoreCase(value) ||
                        method.displayName.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment method: " + value));
    }
}
