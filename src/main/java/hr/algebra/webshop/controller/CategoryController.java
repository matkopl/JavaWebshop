package hr.algebra.webshop.controller;

import hr.algebra.webshop.dto.CategoryDto;
import hr.algebra.webshop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public String getAllCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("newCategory", new CategoryDto());
        return "category/categories";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("newCategory") CategoryDto categoryDto,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.saveCategory(categoryDto);
            redirectAttributes.addFlashAttribute("successMessage", "Kategorija uspješno kreirana!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Greška pri kreiranju kategorije!");
        }
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "category/category-edit";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") CategoryDto categoryDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "category-edit";
        }

        try {
            categoryService.updateCategory(id, categoryDto);
            redirectAttributes.addFlashAttribute("successMessage", "Kategorija uspješno ažurirana!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Greška pri ažuriranju kategorije");
        }

        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kategorija uspješno obrisana!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Greška pri brisanju kategorije!");
        }
        return "redirect:/categories";
    }
}
