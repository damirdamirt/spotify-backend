package com.spotify.spotifybackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.security.Keys; <--- OVO NAM VISE NE TREBA
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec; // <--- OVO JE NOVI IMPORT
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${token.encryption.key}")
    private String secretKey; // Onih 32 karaktera

    @Value("${token.expiration.ms}")
    private long jwtExpiration;

    // Pomocna metoda za kreiranje AES kljuca
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        // Ovde eksplicitno kazemo: "Ovo je AES kljuc"
        return new SecretKeySpec(keyBytes, "AES");
    }

    // 1. Vadjenje korisnickog imena iz tokena
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Genericka metoda za vadjenje bilo kog podatka
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 3. GENERISANJE ENKRIPTOVANOG TOKENA (JWE)
    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                // Koristimo nas ispravan AES kljuc
                .encryptWith(getSigningKey(), Jwts.ENC.A256GCM)
                .compact();
    }

    // 4. Validacija tokena
    public boolean isTokenValid(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 5. DEKRIPCIJA TOKENA
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .decryptWith(getSigningKey()) // Koristimo isti AES kljuc
                .build()
                .parseEncryptedClaims(token)
                .getPayload();
    }
}