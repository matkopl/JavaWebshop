package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.LoginDto;
import hr.algebra.webshop.dto.RegisterDto;
import hr.algebra.webshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) boolean error,
            @RequestParam(value = "logout", required = false) boolean logout,
            Model model
    ) {
        model.addAttribute("loginDto", new LoginDto());
        if (error) model.addAttribute("error", "Neispravno korisničko ime ili lozinka");
        if (logout) model.addAttribute("success", "Uspješno ste se odjavili");
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerDto") RegisterDto registerDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(registerDto);
            redirectAttributes.addFlashAttribute("success", "Registracija uspješna!");
            return "redirect:auth/login";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:auth/register";
        }
    }
}
