package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.OrderDto;
import hr.algebra.webshop.model.Order;
import hr.algebra.webshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    public String getOrderById(@PathVariable Long id, Model model) {
        OrderDto orderDto = orderService.getOrderById(id);
        model.addAttribute("order", orderDto);
        return "order/order-details";
    }

    @GetMapping
    public String getAllOrders(Principal principal, Model model) {
        List<OrderDto> orders = orderService.getOrdersForUser(principal.getName());
        model.addAttribute("orders", orders);
        return "order/orders";
    }
}
