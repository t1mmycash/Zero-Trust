package org.example.dto;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
//@NoArgsConstructor
public class AuthResponse {
    private String accessToken;  // Основной токен для доступа к API
    private String refreshToken; // Токен для обновления accessToken
    private String role;   // Роль пользователя
}
