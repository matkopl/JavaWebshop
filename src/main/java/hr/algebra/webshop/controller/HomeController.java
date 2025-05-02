package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.CategoryDto;
import hr.algebra.webshop.dto.ProductDto;
import hr.algebra.webshop.service.CategoryService;
import hr.algebra.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<CategoryDto> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        List<ProductDto> products = productService.getFeaturedProducts();
        model.addAttribute("products", products);

        return "home";
    }
}
