package org.example.util;

import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
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

    @SneakyThrows
    public String getJtiFromToken(String token) {
        PublicKey publicKey = loadPublicKey();
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getId();
    }

    private PrivateKey loadPrivateKey() throws IOException {
        try (PEMParser pemParser = new PEMParser(
                new InputStreamReader(privateKeyResource.getInputStream())
        )) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            return converter.getPrivateKey(privateKeyInfo);
        }
    }

    private PublicKey loadPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPem = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\r\\n|\\n", "") // Удаляем все переносы строк
                .trim();
        byte[] decoded = Base64.getDecoder().decode(publicKeyPem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }
}
