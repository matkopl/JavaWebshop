package hr.algebra.webshop.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import hr.algebra.webshop.model.Order;
import hr.algebra.webshop.model.PaymentStatus;
import hr.algebra.webshop.repository.OrderRepository;
import hr.algebra.webshop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaypalService {
    private final APIContext apiContext;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public Payment createPayment(Double total, String currency, String returnUrl, String cancelUrl) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Web shop purchase | " + total);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(returnUrl);
        redirectUrls.setCancelUrl(cancelUrl);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecution);
    }

    public hr.algebra.webshop.model.Payment savePaymentDetails(Order order, String paymentId, String payerId) {
        Order savedOrder = orderRepository.save(order);

        hr.algebra.webshop.model.Payment payment = new hr.algebra.webshop.model.Payment();
        payment.setPaymentId(paymentId);
        payment.setPayerId(payerId);
        payment.setOrder(savedOrder);
        payment.setStatus(PaymentStatus.COMPLETED);

        savedOrder.setPayment(payment);

        return paymentRepository.save(payment);
    }


}
