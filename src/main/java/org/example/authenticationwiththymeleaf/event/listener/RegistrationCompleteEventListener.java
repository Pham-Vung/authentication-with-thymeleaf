package org.example.authenticationwiththymeleaf.event.listener;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.authenticationwiththymeleaf.entity.User;
import org.example.authenticationwiththymeleaf.event.entity.RegistrationCompleteEvent;
import org.example.authenticationwiththymeleaf.service.implement.VerificationTokenService;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender mailSender;
    private User user;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // lấy ra user
        user = event.getUser();
        // tạo token cho user
        String token = UUID.randomUUID().toString();
        // lưu token cho user
        verificationTokenService.saveVerificationTokenForUser(user, token);
        // tạo url xác minh
        String url = event.getConfirmationUrl() + "/registration/verifyEmail?token=" + token;
        // gửi mail cho user
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email xác minh";
        String senderName = "Dịch vụ xác minh người dùng";
        String mailContent = "<p> Xin chào, " + user.getFirstName() + ", </p>"
                + "<p> Cảm ơn bạn đã đăng ký với chúng tôi, "
                + "Vui lòng, truy cập vào đường link bên dưới để hoàn tất đăng ký của bạn. </p>"
                + "<a href=\"" + url + "\">Xác minh email của bạn để kích hoạt tài khoản của bạn</a>"
                + "<p>Cảm ơn</p>";
        emailMessage(subject, senderName, mailContent, mailSender, user);
    }

    private static void emailMessage(String subject, String senderName,
                                     String mailContent, JavaMailSender mailSender, User user)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("phamvanvung211003@gmail.com", senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
