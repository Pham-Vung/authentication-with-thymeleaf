package org.example.authenticationwiththymeleaf.dto.request;

import lombok.Data;
import org.example.authenticationwiththymeleaf.entity.Role;

import java.util.List;

@Data
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Role> roles;
}
