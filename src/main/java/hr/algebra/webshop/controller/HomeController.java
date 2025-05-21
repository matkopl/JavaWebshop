package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.CategoryDto;
import hr.algebra.webshop.dto.ProductDto;
import hr.algebra.webshop.service.CartService;
import hr.algebra.webshop.service.CategoryService;
import hr.algebra.webshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final CartService cartService;

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(required = false) Long categoryId, Model model) {

        List<CategoryDto> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);

        List<ProductDto> products;
        if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("cartItems", cartService.getCartItems());
        return "home";
    }
}
