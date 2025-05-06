package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.ProductDto;
import hr.algebra.webshop.service.CategoryService;
import hr.algebra.webshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("newProduct", new ProductDto());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "product/products";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("newProduct") ProductDto productDto,
                              RedirectAttributes redirectAttributes) {
        try {
            productService.saveProduct(productDto);
            redirectAttributes.addFlashAttribute("successMessage", "Proizvod uspješno kreiran!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Greška pri kreiranju proizvoda!" + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "product/product-edit";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") ProductDto productDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.updateProduct(id, productDto);
            redirectAttributes.addFlashAttribute("successMessage", "Proizvod uspješno ažuriran!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Greška pri ažuriranju proizvoda");
        }

        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Proizvod uspješno obrisan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Greška pri brisanju proizvoda!" + e.getMessage());
        }
        return "redirect:/products";
    }
}
