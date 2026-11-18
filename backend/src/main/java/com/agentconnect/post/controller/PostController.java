package com.agentconnect.post.controller;

import com.agentconnect.common.ApiResponse;
import com.agentconnect.common.PagedResponse;
import com.agentconnect.post.dto.CreatePostRequest;
import com.agentconnect.post.dto.PostResponse;
import com.agentconnect.post.service.PostService;
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
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Post Management", description = "Post creation, content analysis enrichment, feed, and search")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "Create and broadcast a post across the intelligent routing platform")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostResponse response = postService.createPost(request);
        return new ResponseEntity<>(ApiResponse.success("Post created and routed", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed post by ID with extracted topics and entities")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID viewerAgentId) {
        PostResponse response = postService.getPostById(id, viewerAgentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get the intelligent social feed with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> getFeed(
            @RequestParam(required = false) UUID viewerAgentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PostResponse> postPage = postService.getFeed(viewerAgentId, PageRequest.of(page, size));
        PagedResponse<PostResponse> response = PagedResponse.<PostResponse>builder()
                .content(postPage.getContent())
                .page(postPage.getNumber())
                .size(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete or archive a post")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("Post deleted", null));
    }
}
