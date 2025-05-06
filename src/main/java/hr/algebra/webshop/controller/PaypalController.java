package hr.algebra.webshop.controller;

import com.paypal.api.payments.Payment;
import hr.algebra.webshop.model.Order;
import hr.algebra.webshop.model.PaymentMethod;
import hr.algebra.webshop.model.User;
import hr.algebra.webshop.service.CartService;
import hr.algebra.webshop.service.OrderService;
import hr.algebra.webshop.service.PaypalService;
import hr.algebra.webshop.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createPayment(Principal principal) {
        Map<String, String> response = new HashMap<>();
        try {
            User user = userService.findByUsername(principal.getName());
            double total = orderService.calculateCartTotal();

            Payment payment = paypalService.createPayment(
                    total,
                    "EUR",
                    "http://localhost:8080/payment/complete",
                    "http://localhost:8080/payment/cancel"
            );

            response.put("id", payment.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Greška pri kreiranju plaćanja: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/complete")
    @Transactional
    @ResponseBody
    public Map<String, Object> completePayment(
            @RequestParam String paymentId,
            @RequestParam String payerId,
            Principal principal) {

        try {
            User user = userService.findByUsername(principal.getName());
            Order order = orderService.createOrderFromCart(user.getUsername(), PaymentMethod.PAYPAL);
            Payment payment = paypalService.executePayment(paymentId, payerId);

            if(!"approved".equalsIgnoreCase(payment.getState())) {
                throw new RuntimeException("PayPal transakcija nije odobrena");
            }

            paypalService.savePaymentDetails(order, paymentId, payerId);
            cartService.clearCart();

            return Map.of("success", true, "orderId", order.getId());
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    @GetMapping("/cancel")
    public String paymentCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Plaćanje otkazano");
        return "redirect:/cart/checkout";
    }
}