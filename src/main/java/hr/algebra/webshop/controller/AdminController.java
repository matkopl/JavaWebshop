package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.LoginHistoryDto;
import hr.algebra.webshop.service.CategoryService;
import hr.algebra.webshop.service.LoginHistoryService;
import hr.algebra.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final LoginHistoryService loginHistoryService;

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        return "admin/admin";
    }

    @GetMapping("/login-history")
    public String loginHistory(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Model model) {
        List<LoginHistoryDto> loginHistoryDtos = loginHistoryService.getFilteredHistory(username, start, end);
        model.addAttribute("loginHistoryDtos", loginHistoryDtos);
        return "admin/login-history";
    }
}
