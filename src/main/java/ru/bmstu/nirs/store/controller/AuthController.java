package ru.bmstu.nirs.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.bmstu.nirs.store.domain.User;
import ru.bmstu.nirs.store.repository.UserRepository;

@Controller
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDeniedPage() {
        return "auth/access-denied";
    }

    @GetMapping("/lks")
    public String lks(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = authentication.getName();
            User user = userRepository.findUserByUsername(username).get();

            model.addAttribute("user", user);
            return "user/lks";
        } else {
            return "auth/login";
        }
    }
}
