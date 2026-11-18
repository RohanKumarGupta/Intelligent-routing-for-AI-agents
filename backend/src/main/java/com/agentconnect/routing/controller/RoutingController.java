package com.agentconnect.routing.controller;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.repository.AgentRepository;
import com.agentconnect.common.ApiResponse;
import com.agentconnect.post.dto.CreatePostRequest;
import com.agentconnect.post.dto.PostResponse;
import com.agentconnect.post.model.PostType;
import com.agentconnect.post.service.PostService;
import com.agentconnect.routing.dto.RoutingEvaluationRequest;
import com.agentconnect.routing.dto.RoutingEvaluationResponse;
import com.agentconnect.routing.model.RoutingConfiguration;
import com.agentconnect.routing.model.RoutingDecision;
import com.agentconnect.routing.repository.RoutingConfigurationRepository;
import com.agentconnect.routing.repository.RoutingDecisionRepository;
import com.agentconnect.routing.service.RelevanceRoutingEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/routing")
@RequiredArgsConstructor
@Tag(name = "Relevance Routing Engine", description = "Evaluate and inspect two-stage routing decisions and weight configurations")
public class RoutingController {

    private final RelevanceRoutingEngine routingEngine;
    private final RoutingDecisionRepository routingDecisionRepository;
    private final RoutingConfigurationRepository routingConfigRepository;
    private final PostService postService;
    private final AgentRepository agentRepository;

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate relevance routing for a post across all registered agent candidates")
    public ResponseEntity<ApiResponse<RoutingEvaluationResponse>> evaluateRouting(@RequestBody RoutingEvaluationRequest request) {
        UUID targetPostId = request.getPostId();

        // If custom content provided without a postId, create a temporary/preview post from Swiggy
        if (targetPostId == null && request.getCustomContent() != null && !request.getCustomContent().isBlank()) {
            Agent swiggy = agentRepository.findByName("Swiggy Commerce Agent")
                    .orElseGet(() -> agentRepository.findAll().stream().findFirst().orElseThrow());

            CreatePostRequest postReq = CreatePostRequest.builder()
                    .authorAgentId(swiggy.getId())
                    .content(request.getCustomContent())
                    .type(PostType.OFFER)
                    .geography("IN")
                    .idempotencyKey("eval-" + System.currentTimeMillis())
                    .build();

            PostResponse postRes = postService.createPost(postReq);
            targetPostId = postRes.getId();
        }

        RoutingEvaluationResponse response = routingEngine.evaluatePost(targetPostId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/decisions/{postId}")
    @Operation(summary = "Get historical persisted routing decisions for a specific post")
    public ResponseEntity<ApiResponse<List<RoutingDecision>>> getDecisionsByPost(@PathVariable UUID postId) {
        List<RoutingDecision> decisions = routingDecisionRepository.findByPostIdOrderByFinalScoreDesc(postId);
        return ResponseEntity.ok(ApiResponse.success(decisions));
    }

    @GetMapping("/configuration")
    @Operation(summary = "Get current active routing scoring weights")
    public ResponseEntity<ApiResponse<RoutingConfiguration>> getActiveConfiguration() {
        RoutingConfiguration config = routingConfigRepository.findByActiveTrue()
                .orElseGet(() -> RoutingConfiguration.builder()
                        .name("Default In-Memory")
                        .build());
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @PutMapping("/configuration")
    @Operation(summary = "Update routing algorithm weights (Admin only)")
    public ResponseEntity<ApiResponse<RoutingConfiguration>> updateConfiguration(@RequestBody RoutingConfiguration newConfig) {
        newConfig.setActive(true);
        RoutingConfiguration saved = routingConfigRepository.save(newConfig);
        return ResponseEntity.ok(ApiResponse.success("Routing weights updated", saved));
    }
}
