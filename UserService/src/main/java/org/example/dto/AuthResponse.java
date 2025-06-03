package org.example.dto;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String role;
}
