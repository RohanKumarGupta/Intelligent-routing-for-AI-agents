package com.agentconnect.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long jwtExpiration;

    public JwtTokenProvider(
            @Value("${app.security.jwt-secret:default-dev-secret-change-in-production-at-least-256-bits-long-key-for-security}") String secret,
            @Value("${app.security.jwt-expiration:86400000}") long jwtExpiration) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(userPrincipal.getId().toString())
                .claim("email", userPrincipal.getEmail())
                .claim("role", userPrincipal.getRole().name())
                .claim("companyId", userPrincipal.getCompanyId() != null ? userPrincipal.getCompanyId().toString() : null)
                .claim("agentId", userPrincipal.getAgentId() != null ? userPrincipal.getAgentId().toString() : null)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateTokenForAgent(UUID agentId, String agentName) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 7); // 7 days for agents

        return Jwts.builder()
                .subject(agentId.toString())
                .claim("email", agentName)
                .claim("role", Role.AGENT.name())
                .claim("agentId", agentId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public UserPrincipal getUserPrincipalFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        UUID id = UUID.fromString(claims.getSubject());
        String email = claims.get("email", String.class);
        String roleStr = claims.get("role", String.class);
        Role role = Role.valueOf(roleStr);
        String companyIdStr = claims.get("companyId", String.class);
        UUID companyId = companyIdStr != null ? UUID.fromString(companyIdStr) : null;
        String agentIdStr = claims.get("agentId", String.class);
        UUID agentId = agentIdStr != null ? UUID.fromString(agentIdStr) : null;

        return UserPrincipal.builder()
                .id(id)
                .email(email)
                .role(role)
                .companyId(companyId)
                .agentId(agentId)
                .authorities(java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.name())))
                .build();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }
}
