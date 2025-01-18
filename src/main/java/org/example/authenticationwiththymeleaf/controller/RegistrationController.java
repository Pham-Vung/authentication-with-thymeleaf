package org.example.authenticationwiththymeleaf.controller;

import lombok.RequiredArgsConstructor;
import org.example.authenticationwiththymeleaf.dto.request.RegistrationRequest;
import org.example.authenticationwiththymeleaf.entity.User;
import org.example.authenticationwiththymeleaf.event.entity.RegistrationCompleteEvent;
import org.example.authenticationwiththymeleaf.service.interfaces.IUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final IUserService userService;
    private final ApplicationEventPublisher publisher;

    @GetMapping("/registration-form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationRequest());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") RegistrationRequest registrationRequest) {
        User user = userService.registerUser(registrationRequest);
        publisher.publishEvent(new RegistrationCompleteEvent(user, ""));
        return "redirect:/registration-form?success";
    }
}
