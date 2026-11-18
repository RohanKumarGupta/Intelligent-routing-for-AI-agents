package com.agentconnect.moderation.controller;

import com.agentconnect.common.ApiResponse;
import com.agentconnect.common.PagedResponse;
import com.agentconnect.moderation.model.ModerationResult;
import com.agentconnect.moderation.model.ModerationResultType;
import com.agentconnect.moderation.repository.ModerationResultRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/moderation")
@RequiredArgsConstructor
@Tag(name = "Moderation & AI Guardrails", description = "Review and inspect automated moderation decisions and safety reports")
public class ModerationController {

    private final ModerationResultRepository moderationResultRepository;

    @GetMapping("/pending")
    @Operation(summary = "Get items flagged for human review or rejected by guardrails")
    public ResponseEntity<ApiResponse<PagedResponse<ModerationResult>>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ModerationResult> resultPage = moderationResultRepository.findByResult(ModerationResultType.REVIEW, PageRequest.of(page, size));
        PagedResponse<ModerationResult> response = PagedResponse.<ModerationResult>builder()
                .content(resultPage.getContent())
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Manually approve a flagged content item")
    public ResponseEntity<ApiResponse<ModerationResult>> approveItem(@PathVariable UUID id) {
        ModerationResult item = moderationResultRepository.findById(id).orElseThrow();
        item.setResult(ModerationResultType.APPROVED);
        return ResponseEntity.ok(ApiResponse.success("Item approved", moderationResultRepository.save(item)));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Manually reject a content item and suspend publishing")
    public ResponseEntity<ApiResponse<ModerationResult>> rejectItem(@PathVariable UUID id) {
        ModerationResult item = moderationResultRepository.findById(id).orElseThrow();
        item.setResult(ModerationResultType.REJECTED);
        item.setPolicyViolation(true);
        return ResponseEntity.ok(ApiResponse.success("Item rejected", moderationResultRepository.save(item)));
    }
}
