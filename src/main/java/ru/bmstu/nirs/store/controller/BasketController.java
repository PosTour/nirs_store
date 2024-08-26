package ru.bmstu.nirs.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.domain.Basket;
import ru.bmstu.nirs.store.service.BasketService;

@Controller
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;

    @Autowired
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        var basket = basketService.findById(id);
        basket.ifPresent(basketService::setItemsQuantity);

        model.addAttribute("basket", basket);
        return "";
    }

    @GetMapping("/find_by_client_id/{id}")
    public String findByClientId(@PathVariable("id") int id, Model model) {
        model.addAttribute("basket", basketService.findByClientId(id));
        return "";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        var basket = basketService.findById(id);
        basket.ifPresent(basketService::setItemsQuantity);

        model.addAttribute("basket", basket);
        return "";
    }

    @PatchMapping("/update/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("basket") Basket basket) {
        basketService.update(id, basket);
        return "";
    }

    @PatchMapping("/add_item/{id}")
    public void addItem(@PathVariable("id") int id, @ModelAttribute("basket") Basket basket) {
        basketService.addItem(id, basket);
    }
}
