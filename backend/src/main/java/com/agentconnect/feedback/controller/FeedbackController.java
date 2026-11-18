package com.agentconnect.feedback.controller;

import com.agentconnect.common.ApiResponse;
import com.agentconnect.feedback.model.Feedback;
import com.agentconnect.feedback.model.FeedbackType;
import com.agentconnect.feedback.repository.FeedbackRepository;
import com.agentconnect.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback & Adaptive Learning", description = "Collect agent engagement feedback to optimize routing precision")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final FeedbackRepository feedbackRepository;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmitFeedbackRequest {
        private UUID agentId;
        private UUID notificationId;
        private FeedbackType type;
    }

    @PostMapping("/posts/{postId}")
    @Operation(summary = "Submit explicit feedback on a post or notification (RELEVANT, NOT_RELEVANT, TOO_FREQUENT, etc.)")
    public ResponseEntity<ApiResponse<Feedback>> submitFeedback(
            @PathVariable UUID postId,
            @RequestBody SubmitFeedbackRequest request) {
        Feedback feedback = feedbackService.recordFeedback(request.getAgentId(), postId, request.getNotificationId(), request.getType());
        return ResponseEntity.ok(ApiResponse.success("Feedback recorded", feedback));
    }

    @GetMapping("/posts/{postId}")
    @Operation(summary = "Get all feedback submitted for a post")
    public ResponseEntity<ApiResponse<List<Feedback>>> getFeedbackByPost(@PathVariable UUID postId) {
        List<Feedback> list = feedbackRepository.findByPostId(postId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/agent/{agentId}")
    @Operation(summary = "Get all feedback submitted by an agent")
    public ResponseEntity<ApiResponse<List<Feedback>>> getFeedbackByAgent(@PathVariable UUID agentId) {
        List<Feedback> list = feedbackRepository.findByAgentId(agentId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
