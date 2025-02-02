package org.example.authenticationwiththymeleaf.repository;

import org.example.authenticationwiththymeleaf.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
