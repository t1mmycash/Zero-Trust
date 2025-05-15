package org.example.util;

import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${rsa.private-key-path}")
    private Resource privateKeyResource;

    @Value("${rsa.public-key-path}")
    private Resource publicKeyResource;

    @Value("${jwt.expirationAccess}")
    private long expirationAccess;

    @Value("${jwt.expirationRefresh}")
    private long expirationRefresh;


    @SneakyThrows
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationAccess);
        PrivateKey privateKey = loadPrivateKey();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("roles", List.of(user.getRole()))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(privateKey)
                .compact();
    }

    @SneakyThrows
    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationRefresh);
        String jti = UUID.randomUUID().toString();
        PrivateKey privateKey = loadPrivateKey();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("roles", List.of(user.getRole()))
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(expiry)
                .id(jti)
                .signWith(privateKey)
                .compact();
    }


    @SneakyThrows
    public boolean validateToken(String token) {
        PublicKey publicKey = loadPublicKey();
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    public AuthResponse refreshTokens(String refreshToken)
//            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        if (!validateToken(refreshToken)) {
//            throw new TokenValidationException(
//                    "Refresh - токен неправильный, просрочен или отозван, аутентифицируйтесь заново");
//        }
//        User user = User.builder()
//                .id(Long.parseLong(getIdFromToken((refreshToken))))
//                .role(getRoleFromToken(refreshToken))
//                .build();
//        return AuthResponse.builder()
//                .role(user.getRole())
//                .accessToken(generateAccessToken(user))
//                .refreshToken(generateRefreshToken(user))
//                .build();
//    }

    public String getIdFromToken(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = loadPublicKey();
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getRoleFromToken(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = loadPublicKey();
        var claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        List<String> roles = claims.get("roles", List.class);
        return roles.get(0);
    }

    public String getJtiFromToken(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = loadPublicKey();
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getId();
    }

    private PrivateKey loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String privateKeyPem = new String(privateKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    private PublicKey loadPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPem = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(publicKeyPem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }
}
