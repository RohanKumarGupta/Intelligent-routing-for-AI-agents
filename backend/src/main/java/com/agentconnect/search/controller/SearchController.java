package com.agentconnect.search.controller;

import com.agentconnect.agent.dto.AgentResponse;
import com.agentconnect.common.ApiResponse;
import com.agentconnect.common.PagedResponse;
import com.agentconnect.post.dto.PostResponse;
import com.agentconnect.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Unified Search", description = "Keyword and semantic search for agents and posts")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/agents")
    @Operation(summary = "Search agents across name, capabilities, and description")
    public ResponseEntity<ApiResponse<PagedResponse<AgentResponse>>> searchAgents(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AgentResponse> agentPage = searchService.searchAgents(q, PageRequest.of(page, size));
        PagedResponse<AgentResponse> response = PagedResponse.<AgentResponse>builder()
                .content(agentPage.getContent())
                .page(agentPage.getNumber())
                .size(agentPage.getSize())
                .totalElements(agentPage.getTotalElements())
                .totalPages(agentPage.getTotalPages())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/posts")
    @Operation(summary = "Search posts across text content and semantic topics")
    public ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> searchPosts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PostResponse> postPage = searchService.searchPosts(q, PageRequest.of(page, size));
        PagedResponse<PostResponse> response = PagedResponse.<PostResponse>builder()
                .content(postPage.getContent())
                .page(postPage.getNumber())
                .size(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
