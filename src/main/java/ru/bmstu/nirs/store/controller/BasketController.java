package ru.bmstu.nirs.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.service.BasketService;

@Controller
@RequestMapping("/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @GetMapping
    public String viewBasket(Model model) {
        var basket = basketService.getCurrentBasket();
        basket.ifPresent(basketService::setItemsQuantity);

        model.addAttribute("basket", basket.get());
        return "order/basket";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        var basket = basketService.findById(id);
        basket.ifPresent(basketService::setItemsQuantity);

        model.addAttribute("basket", basket);
        return "";
    }

    @GetMapping("/user/{id}")
    public String findByUserId(@PathVariable("id") int id, Model model) {
        model.addAttribute("basket", basketService.findByUserId(id));
        return "";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        var basket = basketService.findById(id);
        basket.ifPresent(basketService::setItemsQuantity);

        model.addAttribute("basket", basket);
        return "";
    }

    @PostMapping("/update/{itemId}")
    public String updateItemQuantity(@PathVariable int itemId, @RequestParam int quantity) {
        basketService.updateItemQuantity(itemId, quantity);
        return "redirect:/basket";
    }

    @PostMapping("/add_item/{id}")
    @ResponseBody
    public void addItem(@PathVariable("id") int id) {
        basketService.addItem(id);
    }
}
