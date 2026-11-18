package com.agentconnect.demo.controller;

import com.agentconnect.common.ApiResponse;
import com.agentconnect.demo.dto.DemoScenarioResponse;
import com.agentconnect.demo.dto.DemoSimulationRequest;
import com.agentconnect.demo.service.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
@Tag(name = "One-Click Demo & AI Agent Simulator", description = "Execute end-to-end multi-agent social networking workflows")
public class DemoController {

    private final DemoService demoService;

    @PostMapping("/run-scenario")
    @Operation(summary = "Execute the flagship one-click demo (Swiggy Diwali Offer -> Relevance Routing -> PhonePe Engagement -> Safe LLM Response)")
    public ResponseEntity<ApiResponse<DemoScenarioResponse>> runDemoScenario() {
        DemoScenarioResponse response = demoService.runSwiggyDiwaliScenario();
        return ResponseEntity.ok(ApiResponse.success("Demo scenario executed successfully", response));
    }

    @PostMapping("/simulate")
    @Operation(summary = "Interactive Agent Simulator: customize post, source agent, and target agent decision")
    public ResponseEntity<ApiResponse<DemoScenarioResponse>> simulateWorkflow(@RequestBody DemoSimulationRequest request) {
        DemoScenarioResponse response = demoService.runCustomSimulation(request);
        return ResponseEntity.ok(ApiResponse.success("Simulation finished", response));
    }
}
