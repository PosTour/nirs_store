package ru.bmstu.nirs.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.domain.Item;
import ru.bmstu.nirs.store.service.CategoryService;
import ru.bmstu.nirs.store.service.ItemService;

@Controller
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;

    @GetMapping("/new")
    public String newItem(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("categories", categoryService.findAll());
        return "item/item-form";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("item") Item item, @RequestParam("categoryId") int categoryId) {
        if (item.getQuantity() < 0) {
            throw new IllegalArgumentException("Количество товара не может быть отрицательным.");
        }
        var category = categoryService.findById(categoryId).get();
        item.setCategory(category);
        itemService.save(item);
        return "redirect:/item/find_all";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("item", itemService.findById(id).get());
        model.addAttribute("categories", categoryService.findAll());
        return "item/item-edit-form";
    }

    @PatchMapping("/update/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("item") Item item, @RequestParam("categoryId") int categoryId) {
        var category = categoryService.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));
        item.setCategory(category);
        itemService.update(id, item);
        return "redirect:/item/find_all";
    }

    @PatchMapping("/decrease_quantity/{id}/{amount}")
    public String decreaseQuantity(@PathVariable("id") int id, @PathVariable("amount") int amount) {
        itemService.decreaseStock(id, amount);
        return "";
    }

    @PatchMapping("/increase_quantity/{id}/{amount}")
    public String increaseQuantity(@PathVariable("id") int id, @PathVariable("amount") int amount) {
        itemService.increaseStock(id, amount);
        return "";
    }

    @GetMapping("/find_all")
    public String findAll(Model model) {
        model.addAttribute("items", itemService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("selectedCategoryId", null);
        return "item/item-list";
    }

    @GetMapping("/category/{id}")
    public String findAllByCategoryId(@PathVariable("id") int id, Model model) {
        model.addAttribute("items", itemService.findByCategoryId(id));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("selectedCategoryId", id);
        return "item/item-list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        model.addAttribute("item", itemService.findById(id).get());
        return "item/item";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        itemService.delete(id);
        return "";
    }
}
