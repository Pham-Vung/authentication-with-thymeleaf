package org.example.authenticationwiththymeleaf.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.authenticationwiththymeleaf.entity.User;
import org.example.authenticationwiththymeleaf.entity.VerificationToken;
import org.example.authenticationwiththymeleaf.repository.UserRepository;
import org.example.authenticationwiththymeleaf.repository.VerificationTokenRepository;
import org.example.authenticationwiththymeleaf.service.interfaces.IVerificationTokenService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationTokenService implements IVerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Override
    public String validateToken(String token) {
        Optional<VerificationToken> theToken = verificationTokenRepository.findByToken(token);
        if (theToken.isEmpty()) {
            return "INVALID";
        }

        User user = theToken.get().getUser();
        Calendar calendar = Calendar.getInstance();
        if (theToken.get().getExpirationTime()
                .getTime() - calendar.getTime().getTime() <= 0) {
            return "EXPIRED";
        }

        user.setEnabled(true);
        userRepository.save(user);
        return "VALID";
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        var verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public void deleteUserToken(Long id) {

    }
}
