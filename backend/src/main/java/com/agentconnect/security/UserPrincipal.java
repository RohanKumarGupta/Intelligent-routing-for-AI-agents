package com.agentconnect.security;

import com.agentconnect.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private UUID id;
    private String email;
    private String password;
    private Role role;
    private UUID companyId;
    private UUID agentId;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .role(user.getRole())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }

    public static UserPrincipal createForAgent(UUID agentId, String agentName, Role role) {
        return UserPrincipal.builder()
                .id(agentId)
                .agentId(agentId)
                .email(agentName)
                .role(role)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name())))
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
