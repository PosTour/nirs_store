package ru.bmstu.nirs.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.domain.Item;
import ru.bmstu.nirs.store.domain.Order;
import ru.bmstu.nirs.store.service.OrderService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/new")
    public String newOrder(Model model) {
        model.addAttribute("order", new Order());
        return "";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("order") Order order) {
        orderService.save(order);
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
        return "";
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
