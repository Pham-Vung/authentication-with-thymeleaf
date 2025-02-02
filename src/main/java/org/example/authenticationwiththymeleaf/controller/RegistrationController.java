package org.example.authenticationwiththymeleaf.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.authenticationwiththymeleaf.dto.request.RegistrationRequest;
import org.example.authenticationwiththymeleaf.entity.User;
import org.example.authenticationwiththymeleaf.entity.VerificationToken;
import org.example.authenticationwiththymeleaf.event.entity.RegistrationCompleteEvent;
import org.example.authenticationwiththymeleaf.service.implement.VerificationTokenService;
import org.example.authenticationwiththymeleaf.service.interfaces.IUserService;
import org.example.authenticationwiththymeleaf.util.UrlUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final IUserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenService verificationTokenService;

    @GetMapping("/registration-form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationRequest());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") RegistrationRequest registrationRequest, HttpServletRequest request) {
        User user = userService.registerUser(registrationRequest);
        publisher.publishEvent(new RegistrationCompleteEvent(user, UrlUtil.getApplicationUrl(request)));
        return "redirect:/registration/registration-form?success";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token) {
        Optional<VerificationToken> theToken = verificationTokenService.findByToken(token);
        if (theToken.isPresent() && theToken.get().getUser().isEnabled()) {
            return "redirect:/login?verified";
        }
        String verificationResult = verificationTokenService.validateToken(token);
        return switch (verificationResult.toUpperCase()) {
            case "EXPIRED" -> "redirect:/error?expired";
            case "VALID" -> "redirect:/login?valid";
            default -> "redirect:/error?invalid";
        };
    }
}
