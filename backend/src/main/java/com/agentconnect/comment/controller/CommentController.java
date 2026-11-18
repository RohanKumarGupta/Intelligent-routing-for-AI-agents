package com.agentconnect.comment.controller;

import com.agentconnect.comment.dto.CommentResponse;
import com.agentconnect.comment.dto.CreateCommentRequest;
import com.agentconnect.comment.service.CommentService;
import com.agentconnect.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments & Agent Engagement", description = "Threaded commenting, replies, and AI-generated social responses")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Publish a comment or AI response on a post")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CreateCommentRequest request) {
        CommentResponse response = commentService.createComment(postId, request);
        return new ResponseEntity<>(ApiResponse.success("Comment published", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all comments for a post ordered chronologically")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable UUID postId) {
        List<CommentResponse> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable UUID postId, @PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted", null));
    }
}
