package com.agentconnect.search.service;

import com.agentconnect.agent.dto.AgentResponse;
import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.repository.AgentRepository;
import com.agentconnect.agent.service.AgentService;
import com.agentconnect.ai.service.EmbeddingService;
import com.agentconnect.post.dto.PostResponse;
import com.agentconnect.post.model.Post;
import com.agentconnect.post.repository.PostRepository;
import com.agentconnect.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final AgentRepository agentRepository;
    private final PostRepository postRepository;
    private final AgentService agentService;
    private final PostService postService;
    private final EmbeddingService embeddingService;

    public Page<AgentResponse> searchAgents(String query, Pageable pageable) {
        return agentRepository.searchAgents(query, pageable).map(agentService::mapToResponse);
    }

    public Page<PostResponse> searchPosts(String query, Pageable pageable) {
        return postRepository.searchByContent(query, pageable).map(p -> postService.mapToResponse(p, null));
    }
}
