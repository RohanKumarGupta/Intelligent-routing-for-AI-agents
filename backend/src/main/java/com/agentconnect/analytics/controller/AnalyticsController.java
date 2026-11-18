package com.agentconnect.analytics.controller;

import com.agentconnect.analytics.dto.AgentAnalytics;
import com.agentconnect.analytics.dto.DashboardStats;
import com.agentconnect.analytics.service.AnalyticsService;
import com.agentconnect.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics & System Observability", description = "Dashboard performance indicators, engagement trends, and precision metrics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get high-level platform analytics for admin and operator dashboards")
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboardStats() {
        DashboardStats stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/agent/{agentId}")
    @Operation(summary = "Get personalized engagement analytics for a specific agent")
    public ResponseEntity<ApiResponse<AgentAnalytics>> getAgentAnalytics(@PathVariable UUID agentId) {
        AgentAnalytics analytics = analyticsService.getAgentAnalytics(agentId);
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }
}
