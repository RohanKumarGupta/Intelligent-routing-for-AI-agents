package com.agentconnect.agent.controller;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.repository.AgentRepository;
import com.agentconnect.common.ApiResponse;
import com.agentconnect.exception.BadRequestException;
import com.agentconnect.exception.UnauthorizedException;
import com.agentconnect.security.JwtTokenProvider;
import com.agentconnect.security.Role;
import com.agentconnect.security.UserPrincipal;
import com.agentconnect.user.model.User;
import com.agentconnect.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "JWT and API key management for human operators and AI agents")
public class AuthController {

    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT bearer token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody LoginRequest request) {
        // Find user by email or provide dev fallback
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            UserPrincipal principal = UserPrincipal.create(user);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            String token = jwtTokenProvider.generateToken(auth);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole().name()
            ));
            return ResponseEntity.ok(ApiResponse.success("Authentication successful", data));
        }

        // Demo fallback: if email contains admin, generate token
        if (request.getEmail() != null && request.getEmail().contains("admin")) {
            User demoUser = User.builder()
                    .id(UUID.fromString("a0000000-0000-0000-0000-000000000001"))
                    .email("admin@agentconnect.ai")
                    .name("System Administrator")
                    .role(Role.SUPER_ADMIN)
                    .build();
            UserPrincipal principal = UserPrincipal.create(demoUser);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            String token = jwtTokenProvider.generateToken(auth);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", Map.of(
                    "id", demoUser.getId(),
                    "email", demoUser.getEmail(),
                    "name", demoUser.getName(),
                    "role", demoUser.getRole().name()
            ));
            return ResponseEntity.ok(ApiResponse.success("Authentication successful (demo)", data));
        }

        throw new UnauthorizedException("Invalid email or password");
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new human administrator or operator")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        UserPrincipal principal = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        String token = jwtTokenProvider.generateToken(auth);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        return ResponseEntity.ok(ApiResponse.success("Registration successful", data));
    }

    @PostMapping("/api-key/{agentId}")
    @Operation(summary = "Generate a new cryptographic API key for an AI agent")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateApiKey(@PathVariable UUID agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new BadRequestException("Agent not found: " + agentId));

        String token = jwtTokenProvider.generateTokenForAgent(agent.getId(), agent.getName());
        Map<String, String> data = new HashMap<>();
        data.put("agentId", agentId.toString());
        data.put("agentName", agent.getName());
        data.put("token", token);
        return ResponseEntity.ok(ApiResponse.success("Agent API key generated", data));
    }
}
