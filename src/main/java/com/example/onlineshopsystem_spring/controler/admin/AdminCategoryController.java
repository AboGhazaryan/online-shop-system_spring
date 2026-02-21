package com.example.onlineshopsystem_spring.controler.admin;

import com.example.onlineshopsystem_spring.model.Category;
import com.example.onlineshopsystem_spring.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/admin/categories")
    public String categories(ModelMap modelMap) {
        List<Category> categories = categoryService.findAll();
        modelMap.addAttribute("categories", categories);
        return "adminPackage/adminCategories";
    }

    @GetMapping("/admin/category/add")
    public String addCategory() {
        return "adminPackage/addCategory";
    }

    @PostMapping("/admin/category/add")
    public String addCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/category/delete")
    public String deleteCategory(@RequestParam Integer categoryId, RedirectAttributes redirectAttributes) {
        boolean deleted = categoryService.deleteCategoryById(categoryId);

        if (!deleted) {
            redirectAttributes.addFlashAttribute("errorMessage","The category cannot be deleted because it is already owned by a product");
        }else{
            redirectAttributes.addFlashAttribute("infoMessage","Category has been deleted successfully.");
        }
        return "redirect:/admin/categories";
    }
}
