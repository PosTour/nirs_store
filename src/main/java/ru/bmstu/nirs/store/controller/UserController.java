package ru.bmstu.nirs.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.domain.User;
import ru.bmstu.nirs.store.service.UserService;

@Controller
@RequestMapping("admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("item") User user) {
        userService.save(user);
        return "";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "";
    }

    @PatchMapping("/update/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("item") User user) {
        userService.update(id, user);
        return "";
    }

    @GetMapping("find_all")
    public String findAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        model.addAttribute("item", userService.findById(id));
        return "";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        userService.delete(id);
        return "";
    }
}
