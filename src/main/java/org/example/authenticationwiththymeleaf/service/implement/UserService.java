package org.example.authenticationwiththymeleaf.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.authenticationwiththymeleaf.dto.request.RegistrationRequest;
import org.example.authenticationwiththymeleaf.entity.Role;
import org.example.authenticationwiththymeleaf.entity.User;
import org.example.authenticationwiththymeleaf.repository.UserRepository;
import org.example.authenticationwiththymeleaf.service.interfaces.IUserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest registrationRequest) {
        User user = new User(
                registrationRequest.getFirstName(),
                registrationRequest.getLastName(),
                registrationRequest.getEmail(),
                registrationRequest.getPassword(),
                List.of(new Role("ROLE_USER"))
        );
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }
}
