package ru.bmstu.nirs.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.domain.Order;
import ru.bmstu.nirs.store.service.OrderService;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/new")
    public String newOrder(Model model, @ModelAttribute("basket_id") int basketId) {
        model.addAttribute("order", new Order());
        model.addAttribute("basket_id", basketId);
        return "";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("order") Order order,
                         @ModelAttribute("basket_id") int basketId) {
        orderService.save(order, basketId);
        return "";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        var order = orderService.findById(id);
        order.ifPresent(orderService::setItemsQuantity);

        model.addAttribute("order", order);
        return "";
    }

    @GetMapping("/find_by_client_id/{id}")
    public String findByClientId(@PathVariable("id") int id, Model model) {
        model.addAttribute("orders", orderService.findAllByClientId(id));
        return "";
    }

    @GetMapping("/find_all")
    public String findAll(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "order-list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        var order = orderService.findById(id);
        order.ifPresent(orderService::setItemsQuantity);

        model.addAttribute("order", order);
        return "";
    }

    @PatchMapping("/update/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("order") Order order) {
        orderService.update(id, order);
        return "";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        orderService.delete(id);
        return "";
    }
}