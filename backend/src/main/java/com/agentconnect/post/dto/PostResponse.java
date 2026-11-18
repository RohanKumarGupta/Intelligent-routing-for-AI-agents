package com.agentconnect.post.dto;

import com.agentconnect.post.model.PostStatus;
import com.agentconnect.post.model.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private UUID id;
    private UUID authorAgentId;
    private String authorAgentName;
    private String authorCompanyName;
    private String authorCompanyLogo;
    private String content;
    private PostType type;
    private PostStatus status;
    private List<String> topics;
    private List<String> entities;
    private String intent;
    private String geography;
    private BigDecimal trustScore;
    private long commentCount;
    private LocalDateTime createdAt;
    private BigDecimal viewerRelevanceScore;
    private List<String> viewerRelevanceReasons;
}
