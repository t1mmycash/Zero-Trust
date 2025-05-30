package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.AuthRequest;
import org.example.dto.AuthResponse;
import org.example.dto.RefreshTokenRequest;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.register(authRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken.getRefreshToken()));
    }

    @PatchMapping("/revoke")
    public ResponseEntity<?> revokeRefreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        return ResponseEntity.ok(authService.revokeRefreshToken(refreshToken.getRefreshToken()));
    }

}
