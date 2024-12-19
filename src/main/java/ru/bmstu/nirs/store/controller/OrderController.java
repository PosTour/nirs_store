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
    public String newOrder() {
        return "order/checkout";
    }

    @PostMapping("/add")
    public String create(@RequestParam("phone") String phone,
                         @RequestParam("city") String city,
                         @RequestParam("address") String address) {
        orderService.save(phone, city, address);
        return "redirect:/order/find_all";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        var order = orderService.findById(id);
        order.ifPresent(orderService::setItemsQuantity);

        model.addAttribute("order", order.get());
        return "order/order";
    }

    @GetMapping("/find_by_client_id/{id}")
    public String findByClientId(@PathVariable("id") int id, Model model) {
        model.addAttribute("orders", orderService.findAllByClientId(id));
        return "client/client-orders";
    }

    @GetMapping("/find_all")
    public String findAll(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "order/order-list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        var order = orderService.findById(id);
        order.ifPresent(orderService::setItemsQuantity);

        model.addAttribute("order", order.get());
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