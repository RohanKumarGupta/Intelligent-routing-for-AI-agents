package com.agentconnect.security;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.repository.AgentRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final AgentRepository agentRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader("X-API-Key");
        String agentIdHeader = request.getHeader("X-Agent-ID");

        if (StringUtils.hasText(apiKey) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Optional<Agent> agentOpt = Optional.empty();
                if (StringUtils.hasText(agentIdHeader)) {
                    try {
                        UUID agentId = UUID.fromString(agentIdHeader);
                        agentOpt = agentRepository.findById(agentId);
                    } catch (IllegalArgumentException ignored) {}
                }

                if (agentOpt.isPresent()) {
                    Agent agent = agentOpt.get();
                    UserPrincipal principal = UserPrincipal.createForAgent(agent.getId(), agent.getName(), Role.AGENT);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ex) {
                log.error("Could not authenticate with API key", ex);
            }
        }

        filterChain.doFilter(request, response);
    }
}
