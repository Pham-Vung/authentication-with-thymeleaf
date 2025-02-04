package org.example.authenticationwiththymeleaf.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.authenticationwiththymeleaf.entity.PasswordResetToken;
import org.example.authenticationwiththymeleaf.entity.User;
import org.example.authenticationwiththymeleaf.repository.PasswordResetTokenRepository;
import org.example.authenticationwiththymeleaf.repository.UserRepository;
import org.example.authenticationwiththymeleaf.service.interfaces.IPasswordResetTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService implements IPasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String validatePasswordResetToken(String theToken) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(theToken);
        if (passwordResetToken.isEmpty()) {
            return "INVALID";
        }
        Calendar calendar = Calendar.getInstance();
        if (passwordResetToken.get().getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            return "EXPIRED";
        }
        return "VALID";
    }

    @Override
    public Optional<User> findUserByPasswordResetToken(String theToken) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(theToken);
        return passwordResetToken.map(PasswordResetToken::getUser);
    }

    @Override
    public void resetPassword(User theUser, String password) {
        theUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(theUser);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String passwordResetToken) {
        PasswordResetToken resetToken = new PasswordResetToken(passwordResetToken, user);
        passwordResetTokenRepository.save(resetToken);
    }
}
