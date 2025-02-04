package org.example.authenticationwiththymeleaf.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.authenticationwiththymeleaf.dto.request.RegistrationRequest;
import org.example.authenticationwiththymeleaf.entity.User;
import org.example.authenticationwiththymeleaf.entity.VerificationToken;
import org.example.authenticationwiththymeleaf.event.entity.RegistrationCompleteEvent;
import org.example.authenticationwiththymeleaf.event.listener.RegistrationCompleteEventListener;
import org.example.authenticationwiththymeleaf.service.implement.VerificationTokenService;
import org.example.authenticationwiththymeleaf.service.interfaces.IPasswordResetTokenService;
import org.example.authenticationwiththymeleaf.service.interfaces.IUserService;
import org.example.authenticationwiththymeleaf.util.UrlUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final IUserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenService verificationTokenService;
    private final IPasswordResetTokenService passwordResetTokenService;
    private final RegistrationCompleteEventListener eventListener;

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

    @GetMapping("/forgot-password-request")
    public String forgotPasswordForm() {
        return "forgot-password-form";
    }

    @PostMapping("/forgot-password")
    public String resetPasswordRequest(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        Optional<User> user = userService.findByEmail(email);

        if (user.isEmpty()) {
            return "redirect:/registration/forgot-password-request?not_found";
        }

        String passwordResetToken = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(user.get(), passwordResetToken);

        // gửi email xác thực đặt lại mật khẩu
        String url = UrlUtil.getApplicationUrl(request) + "/registration/password-reset-form?token=" + passwordResetToken;
        try {
            eventListener.sendPasswordResetVerificationEmail(url, user.get());
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/registration/forgot-password-request?success";
    }

    @GetMapping("/password-reset-form")
    public String passwordResetForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "password-reset-form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(HttpServletRequest request) {
        String theToken = request.getParameter("token");
        String password = request.getParameter("password");
        String tokenVerificationResult = passwordResetTokenService.validatePasswordResetToken(theToken);

        if (!tokenVerificationResult.equalsIgnoreCase("VALID")) {
            return "redirect:/error?invalid_token";
        }

        Optional<User> theUser = passwordResetTokenService.findUserByPasswordResetToken(theToken);
        if (theUser.isPresent()) {
            passwordResetTokenService.resetPassword(theUser.get(), password);
            return "redirect:/login?reset_success";
        }
        return "redirect:/error?not_found";
    }
}
