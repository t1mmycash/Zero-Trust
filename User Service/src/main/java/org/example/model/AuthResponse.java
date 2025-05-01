package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;  // Основной токен для доступа к API
    private String refreshToken; // Токен для обновления accessToken
    private String username;     // Имя пользователя (опционально)
    private String role;   // Роли пользователя (опционально)
}
