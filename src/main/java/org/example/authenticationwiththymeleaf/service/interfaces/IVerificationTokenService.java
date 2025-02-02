package org.example.authenticationwiththymeleaf.service.interfaces;

import org.example.authenticationwiththymeleaf.entity.User;
import org.example.authenticationwiththymeleaf.entity.VerificationToken;

import java.util.Optional;

public interface IVerificationTokenService {
    String validateToken(String token);

    void saveVerificationTokenForUser(User user, String token);

    Optional<VerificationToken> findByToken(String token);

    void deleteUserToken(Long id);
}
