package com.kgisl.pos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenProvider {

    @Value("${app.jwt.secret:mySecretKeyForInsuranceAppThatIsAtLeast32CharactersLongForHS256Algorithm}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        String role = userPrincipal.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        // Ensure role has ROLE_ prefix
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
        
        System.out.println("[TOKEN-PROVIDER] Generated token for user: " + userPrincipal.getUsername() + " with role: " + role);
        return token;
    }

    public String generateTokenFromUsername(String username, String role) {
        System.out.println("[TOKEN-PROVIDER] generateTokenFromUsername - Input: username=" + username + ", role=" + role);
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        // Ensure role has ROLE_ prefix for Spring Security
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        
        System.out.println("[TOKEN-PROVIDER] Role with prefix: " + roleWithPrefix);

        String token = Jwts.builder()
                .subject(username)
                .claim("role", roleWithPrefix)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
        
        System.out.println("[TOKEN-PROVIDER] Token generated successfully for: " + username);
        return token;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String role = (String) claims.get("role");
        System.out.println("[TOKEN-PROVIDER] Retrieved role from token: " + role);
        return role;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            System.out.println("[TOKEN-PROVIDER] Token validation successful");
            return true;
        } catch (Exception ex) {
            System.out.println("[TOKEN-PROVIDER] Token validation failed: " + ex.getMessage());
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
