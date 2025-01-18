package org.example.authenticationwiththymeleaf.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable) // tắt tính năng bảo vệ CSRF
                .authorizeHttpRequests(auth -> auth // Cấu hình phân quyền cho các yêu cầu
                        .requestMatchers("/", "/registration/**").permitAll() // Cho phép truy cập không cần xác thực đến các URL này
                        .anyRequest().authenticated() // Yêu cầu xác thực cho tất cả các URL còn lại
                )
                .formLogin(login -> login // Cấu hình đăng nhập
                        .loginPage("/login")
                        .usernameParameter("email") // Sử dụng email làm tham số tên người dùng
                        .defaultSuccessUrl("/") // URL mặc định sau khi đăng nhập thành công
                        .permitAll() // Cho phép tất cả truy cập vào trang đăng nhập
                )
                .logout(logout -> logout // Cấu hình đăng xuất
                        .invalidateHttpSession(true) // Vô hiệu hóa session sau khi đăng xuất
                        .clearAuthentication(true) // Xóa thông tin xác thực
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                ) // Chuyển hướng sau khi đăng xuất thành công
                .build();
    }
}
