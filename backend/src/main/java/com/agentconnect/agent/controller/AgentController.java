package com.agentconnect.agent.controller;

import com.agentconnect.agent.dto.AgentRegistrationRequest;
import com.agentconnect.agent.dto.AgentResponse;
import com.agentconnect.agent.service.AgentService;
import com.agentconnect.common.ApiResponse;
import com.agentconnect.common.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Tag(name = "Agent Management", description = "Registration, discovery, profiles, and verification of AI agents")
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    @Operation(summary = "Register a new AI agent with company, capabilities, and interests")
    public ResponseEntity<ApiResponse<AgentResponse>> registerAgent(@Valid @RequestBody AgentRegistrationRequest request) {
        AgentResponse response = agentService.registerAgent(request);
        return new ResponseEntity<>(ApiResponse.success("Agent registered successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get agent profile and metadata by ID")
    public ResponseEntity<ApiResponse<AgentResponse>> getAgentById(@PathVariable UUID id) {
        AgentResponse response = agentService.getAgentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "List all registered AI agents with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<AgentResponse>>> getAllAgents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AgentResponse> agentPage = agentService.getAllAgents(PageRequest.of(page, size));
        PagedResponse<AgentResponse> response = PagedResponse.<AgentResponse>builder()
                .content(agentPage.getContent())
                .page(agentPage.getNumber())
                .size(agentPage.getSize())
                .totalElements(agentPage.getTotalElements())
                .totalPages(agentPage.getTotalPages())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify an agent (Admin only)")
    public ResponseEntity<ApiResponse<AgentResponse>> verifyAgent(@PathVariable UUID id) {
        AgentResponse response = agentService.verifyAgent(id);
        return ResponseEntity.ok(ApiResponse.success("Agent verified successfully", response));
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend an agent (Admin only)")
    public ResponseEntity<ApiResponse<AgentResponse>> suspendAgent(@PathVariable UUID id) {
        AgentResponse response = agentService.suspendAgent(id);
        return ResponseEntity.ok(ApiResponse.success("Agent suspended", response));
    }
}
