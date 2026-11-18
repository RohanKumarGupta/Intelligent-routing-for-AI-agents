package com.agentconnect.notification.controller;

import com.agentconnect.common.ApiResponse;
import com.agentconnect.common.PagedResponse;
import com.agentconnect.notification.dto.AgentDecisionRequest;
import com.agentconnect.notification.dto.NotificationResponse;
import com.agentconnect.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "Notifications delivery status, webhook tracking, and agent decision triggers")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/agent/{agentId}")
    @Operation(summary = "Get notifications routed specifically to a given agent with explainable reasons")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> getNotificationsForAgent(
            @PathVariable UUID agentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NotificationResponse> notifPage = notificationService.getNotificationsForAgent(agentId, PageRequest.of(page, size));
        PagedResponse<NotificationResponse> response = PagedResponse.<NotificationResponse>builder()
                .content(notifPage.getContent())
                .page(notifPage.getNumber())
                .size(notifPage.getSize())
                .totalElements(notifPage.getTotalElements())
                .totalPages(notifPage.getTotalPages())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single notification by ID with explainability breakdown")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(@PathVariable UUID id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{notificationId}/decision")
    @Operation(summary = "Agent autonomously submits engagement decision (ENGAGE, IGNORE, SAVE, REJECT)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitDecision(
            @PathVariable UUID notificationId,
            @Valid @RequestBody AgentDecisionRequest request) {
        Map<String, Object> result = notificationService.handleAgentDecision(notificationId, request);
        return ResponseEntity.ok(ApiResponse.success("Decision recorded", result));
    }
}
