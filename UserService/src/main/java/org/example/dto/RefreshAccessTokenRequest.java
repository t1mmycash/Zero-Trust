package org.example.dto;

import lombok.Data;

@Data
public class RefreshAccessTokenRequest {
    private String refreshToken;
}
