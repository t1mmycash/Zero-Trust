package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exceptions.TokenValidationException;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.exceptions.UserNotFoundException;
import org.example.exceptions.WrongPasswordException;
import org.example.model.AuthRequest;
import org.example.model.AuthResponse;
import org.example.model.User;
import org.example.storage.UserStorage;
import org.example.util.JwtUtil;
import org.example.util.Roles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserStorage userStorage;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(AuthRequest authRequest) {
        if(userStorage.existsByUsername(authRequest.getLogin())) {
            throw new UserAlreadyExistsException(
                    String.format("Пользователь с логином = %s уже есть", authRequest.getLogin()));
        }
        userStorage.saveAndFlush(User.builder()
                .role(Roles.USER.toString())
                .username(authRequest.getLogin())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .build());
        return "Пользователь зарегистрирован";
    }

    public AuthResponse login(AuthRequest authRequest) {
        User user = userStorage.findByUsername(authRequest.getLogin())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователя с логином = %s не сюществует", authRequest.getLogin())));
        if(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            String newRefreshToken = jwtUtil.generateRefreshToken(user);
            user.setRefreshToken(newRefreshToken);
            userStorage.saveAndFlush(user);
            return AuthResponse.builder()
                    .username(user.getUsername())
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
        User user = userStorage.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UserNotFoundException("Вероятно токен отозван"));
        return AuthResponse.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .refreshToken(refreshToken)
                .accessToken(jwtUtil.generateAccessToken(user))
                .build();
    }


}
