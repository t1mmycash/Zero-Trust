package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exceptions.TokenValidationException;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.exceptions.UserNotFoundException;
import org.example.exceptions.WrongPasswordException;
import org.example.dto.AuthRequest;
import org.example.dto.AuthResponse;
import org.example.model.User;
import org.example.storage.UserRepository;
import org.example.util.JwtUtil;
import org.example.util.Roles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public String register(AuthRequest authRequest) {
        if(userRepository.existsByUsername(authRequest.getLogin())) {
            throw new UserAlreadyExistsException(
                    String.format("Пользователь с логином = %s уже есть", authRequest.getLogin()));
        }
        userRepository.saveAndFlush(User.builder()
                .role(Roles.USER.toString())
                .username(authRequest.getLogin())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .build());
        return "Пользователь зарегистрирован";
    }

    public AuthResponse login(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getLogin())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователя с логином = %s не сюществует", authRequest.getLogin())));
        if(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            String newRefreshToken = jwtUtil.generateRefreshToken(user);
            user.setRefreshToken(newRefreshToken);
            userRepository.saveAndFlush(user);
            return AuthResponse.builder()
                    .role(user.getRole())
                    .accessToken(jwtUtil.generateAccessToken(user))
                    .refreshToken(newRefreshToken)
                    .build();
        } else {
            throw new WrongPasswordException("Пароль неверный");
        }
    }

    public AuthResponse refresh(String refreshToken) {
        if(!jwtUtil.validateToken(refreshToken)) {
            throw new TokenValidationException(
                    "Refresh - токен неправильный, просрочен или отозван, аутентифицируйтесь заново");

        }
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UserNotFoundException("Вероятно токен отозван"));
        return AuthResponse.builder()
                .role(user.getRole())
                .refreshToken(refreshToken)
                .accessToken(jwtUtil.generateAccessToken(user))
                .build();
    }
}
