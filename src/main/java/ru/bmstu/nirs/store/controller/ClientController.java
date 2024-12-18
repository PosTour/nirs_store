package ru.bmstu.nirs.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.domain.Client;
import ru.bmstu.nirs.store.service.ClientService;

@Controller
@RequestMapping("client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/new")
    public String newClient(Model model) {
        model.addAttribute("client", new Client());
        return "client/client-form";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("client") @Valid Client client,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "client/client-form";
        }
        clientService.save(client);
        return "redirect:/client/find_all";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("client", clientService.findById(id).get());
        return "client/client-edit-form";
    }

    @PatchMapping("/update/{id}")
    public String update(@PathVariable("id") int id,
                         @ModelAttribute("client") @Valid Client client,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "client/client-edit-form";
        }
        clientService.update(id, client);
        return "redirect:/client/find_all";
    }

    @GetMapping("phone/{phone}")
    public String findByPhone(@PathVariable("phone") String phone, Model model) {
        model.addAttribute("client", clientService.findByPhone(phone).get());
        return "";
    }

    @GetMapping("/find_all")
    public String findAll(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "client/client-list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        model.addAttribute("client", clientService.findById(id).get());
        return "client/client";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        clientService.delete(id);
        return "";
    }
}
