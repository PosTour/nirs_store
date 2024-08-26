package ru.bmstu.nirs.store.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.domain.Category;
import ru.bmstu.nirs.store.service.CategoryService;

@Controller
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/new")
    public String newCategory(Model model) {
        model.addAttribute("category", new Category());
        return "";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("category") @Valid Category category,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "";
        }
        categoryService.save(category);
        return "";
    }

    @GetMapping("/find_all")
    public String findAll(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        categoryService.delete(id);
        return "";
    }
}
