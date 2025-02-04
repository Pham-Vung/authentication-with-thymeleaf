package org.example.authenticationwiththymeleaf.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.authenticationwiththymeleaf.util.TokenExpirationTime;

import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private Date expirationTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        expirationTime = TokenExpirationTime.getExpirationTime();
    }
}
