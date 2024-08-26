package ru.bmstu.nirs.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.domain.Item;
import ru.bmstu.nirs.store.service.CategoryService;
import ru.bmstu.nirs.store.service.ItemService;

@Controller
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;

    @Autowired
    public ItemController(ItemService itemService, CategoryService categoryService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
    }

    @GetMapping("/new")
    public String newItem(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("categories", categoryService.findAll());
        return "";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("item") Item item) {
        itemService.save(item);
        return "";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("item", itemService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        return "";
    }

    @PatchMapping("/update/{id}")
    public String update(@PathVariable int id, @ModelAttribute("item") Item item) {
        itemService.update(id, item);
        return "";
    }

    @GetMapping("find_all")
    public String findAll(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "";
    }

    @GetMapping("find_all_by_category_id/{id}")
    public String findAllByCategoryId(@PathVariable int id, Model model) {
        model.addAttribute("items", itemService.findByCategoryId(id));
        return "";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        model.addAttribute("item", itemService.findById(id));
        return "";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        itemService.delete(id);
        return "";
    }
}
