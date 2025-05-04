package hr.algebra.webshop.repository;

import hr.algebra.webshop.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
