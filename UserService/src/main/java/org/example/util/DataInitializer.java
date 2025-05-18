package org.example.util;

import lombok.RequiredArgsConstructor;
import org.example.model.User;
import org.example.storage.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void run(String... args) throws Exception {
        userRepository.saveAndFlush(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(Roles.ADMIN.name())
                .build());
    }
}
