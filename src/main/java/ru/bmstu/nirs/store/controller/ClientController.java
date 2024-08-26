package ru.bmstu.nirs.store.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.nirs.store.domain.Client;
import ru.bmstu.nirs.store.service.ClientService;

@Controller
@RequestMapping("client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/new")
    public String newClient(Model model) {
        model.addAttribute("client", new Client());
        return "";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("client") @Valid Client client,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "";
        }
        clientService.save(client);
        return "";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("client", clientService.findById(id));
        return "";
    }

    @PatchMapping("/update/{id}")
    public String update(@PathVariable int id,
                         @ModelAttribute("client") @Valid Client client,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "";
        }
        clientService.update(id, client);
        return "";
    }

    @GetMapping("/{phone}")
    public String findByPhone(@PathVariable("phone") String phone, Model model) {
        model.addAttribute("client", clientService.findByPhone(phone));
        return "";
    }

    @GetMapping("/find_all")
    public String findAll(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        clientService.delete(id);
        return "";
    }
}
