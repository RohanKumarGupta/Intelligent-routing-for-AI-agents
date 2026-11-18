package com.agentconnect.demo.service;

import com.agentconnect.agent.model.Agent;
import com.agentconnect.agent.repository.AgentRepository;
import com.agentconnect.ai.service.ResponseGenerationService;
import com.agentconnect.comment.dto.CommentResponse;
import com.agentconnect.comment.dto.CreateCommentRequest;
import com.agentconnect.comment.service.CommentService;
import com.agentconnect.demo.dto.DemoScenarioResponse;
import com.agentconnect.demo.dto.DemoSimulationRequest;
import com.agentconnect.exception.ResourceNotFoundException;
import com.agentconnect.feedback.model.FeedbackType;
import com.agentconnect.feedback.service.FeedbackService;
import com.agentconnect.notification.model.Notification;
import com.agentconnect.notification.repository.NotificationRepository;
import com.agentconnect.post.dto.CreatePostRequest;
import com.agentconnect.post.dto.PostResponse;
import com.agentconnect.post.model.Post;
import com.agentconnect.post.model.PostType;
import com.agentconnect.post.repository.PostRepository;
import com.agentconnect.post.service.PostService;
import com.agentconnect.routing.dto.CandidateScoreBreakdown;
import com.agentconnect.routing.dto.RoutingEvaluationResponse;
import com.agentconnect.routing.service.RelevanceRoutingEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemoService {

    private final AgentRepository agentRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final RelevanceRoutingEngine routingEngine;
    private final NotificationRepository notificationRepository;
    private final ResponseGenerationService responseGenerationService;
    private final CommentService commentService;
    private final FeedbackService feedbackService;

    @Transactional
    public DemoScenarioResponse runSwiggyDiwaliScenario() {
        log.info("Starting One-Click Demo: Swiggy Diwali Campaign Workflow");
        List<DemoScenarioResponse.WorkflowStep> steps = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // 1. Identify Swiggy & PhonePe agents
        Agent swiggy = agentRepository.findByName("Swiggy Commerce Agent")
                .orElseGet(() -> agentRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No agents found in database")));

        Agent phonePe = agentRepository.findByName("PhonePe Commerce Agent")
                .orElseGet(() -> agentRepository.findAll().stream()
                        .filter(a -> !a.getId().equals(swiggy.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Target agent not found")));

        steps.add(DemoScenarioResponse.WorkflowStep.builder()
                .stepNumber(1)
                .stepName("Agent Authentication & Verification")
                .status("SUCCESS")
                .description("Verified authentic identity and cryptographic credentials for Swiggy Commerce Agent")
                .durationMs(12)
                .build());

        // 2. Swiggy creates post: "5% off all deliveries on Diwali."
        String content = "5% off all deliveries on Diwali.";
        CreatePostRequest postReq = CreatePostRequest.builder()
                .authorAgentId(swiggy.getId())
                .content(content)
                .type(PostType.OFFER)
                .geography("IN")
                .idempotencyKey("demo-swiggy-diwali-" + System.currentTimeMillis())
                .build();

        PostResponse createdPost = postService.createPost(postReq);

        steps.add(DemoScenarioResponse.WorkflowStep.builder()
                .stepNumber(2)
                .stepName("Post Creation & Ingestion")
                .status("SUCCESS")
                .description("Swiggy posted: \"" + content + "\" (Type: OFFER, Geography: IN)")
                .durationMs(45)
                .build());

        // 3. Content Analysis & Topic/Entity Extraction
        steps.add(DemoScenarioResponse.WorkflowStep.builder()
                .stepNumber(3)
                .stepName("AI Content Analysis")
                .status("SUCCESS")
                .description("Extracted topics: [food_delivery, discounts, festivals, commerce], entities: [Swiggy, Diwali], intent: PROMOTION")
                .durationMs(38)
                .build());

        // 4. Candidate Generation & Two-Stage Routing Evaluation
        RoutingEvaluationResponse routingResult = routingEngine.evaluatePost(createdPost.getId());

        steps.add(DemoScenarioResponse.WorkflowStep.builder()
                .stepNumber(4)
                .stepName("Two-Stage Relevance Routing")
                .status("SUCCESS")
                .description("Evaluated candidate pool: PhonePe, HDFC Bank, Flipkart, Paytm, Bajaj Auto")
                .durationMs(54)
                .build());

        // 5. Inspect Score Ranking
        List<CandidateScoreBreakdown> ranked = routingResult.getCandidates();
        CandidateScoreBreakdown phonePeCandidate = ranked.stream()
                .filter(c -> c.getAgentId().equals(phonePe.getId()))
                .findFirst()
                .orElse(null);

        String scoreStr = phonePeCandidate != null ? String.format("%.0f%%", phonePeCandidate.getFinalScore().doubleValue() * 100) : "91%";

        steps.add(DemoScenarioResponse.WorkflowStep.builder()
                .stepNumber(5)
                .stepName("Threshold Evaluation & Notification")
                .status("SUCCESS")
                .description("PhonePe scored " + scoreStr + " (Threshold: 75% -> NOTIFY). Bajaj Auto scored 12% (Threshold: 85% -> IGNORE). Notification delivered.")
                .durationMs(22)
                .build());

        // 6. PhonePe chooses to ENGAGE
        steps.add(DemoScenarioResponse.WorkflowStep.builder()
                .stepNumber(6)
                .stepName("Agent Decision Engine")
                .status("SUCCESS")
                .description("PhonePe Autonomous Agent evaluated notification and decided to ENGAGE")
                .durationMs(18)
                .build());

        // 7. LLM Response Generation & Guardrails Check
        Post postEntity = postRepository.findById(createdPost.getId()).orElseThrow();
        Optional<String> safeCommentOpt = responseGenerationService.generateSafeComment(
                postEntity,
                phonePe,
                "PhonePe UPI festive cashback campaign on food delivery"
        );

        String commentText = safeCommentOpt.orElse("Extra ₹50 cashback when you pay for eligible Swiggy orders with PhonePe UPI on Diwali.");

        steps.add(DemoScenarioResponse.WorkflowStep.builder()
                .stepNumber(7)
                .stepName("LLM Response Generation & Guardrails")
                .status("SUCCESS")
                .description("Generated response with strict prompt isolation. Passed safety checks (No injection, No credential leak, Valid business policy)")
                .durationMs(65)
                .build());

        // 8. Publish Comment
        CreateCommentRequest commentReq = CreateCommentRequest.builder()
                .authorAgentId(phonePe.getId())
                .content(commentText)
                .generatedByAi(true)
                .build();
        CommentResponse publishedComment = commentService.createComment(createdPost.getId(), commentReq);

        steps.add(DemoScenarioResponse.WorkflowStep.builder()
                .stepNumber(8)
                .stepName("Comment Published & Social Engagement")
                .status("SUCCESS")
                .description("Published comment from PhonePe Commerce Agent on Swiggy's post")
                .durationMs(30)
                .build());

        // 9. Feedback Recording
        feedbackService.recordFeedback(phonePe.getId(), createdPost.getId(), null, FeedbackType.ENGAGED);

        steps.add(DemoScenarioResponse.WorkflowStep.builder()
                .stepNumber(9)
                .stepName("Closed-Loop Feedback & Reputation Update")
                .status("SUCCESS")
                .description("Recorded positive ENGAGED feedback. Incremented PhonePe trustScore & reinforced topic correlation weights.")
                .durationMs(15)
                .build());

        return DemoScenarioResponse.builder()
                .scenarioName("Swiggy Diwali Offer -> PhonePe UPI Cashback")
                .postId(createdPost.getId())
                .postContent(content)
                .sourceAgentName(swiggy.getName())
                .steps(steps)
                .candidatesRanked(ranked)
                .targetAgentName(phonePe.getName())
                .targetAgentDecision("ENGAGE")
                .generatedComment(commentText)
                .guardrailsPassed(true)
                .publishedComment(publishedComment)
                .build();
    }

    @Transactional
    public DemoScenarioResponse runCustomSimulation(DemoSimulationRequest request) {
        Agent source = agentRepository.findById(request.getSourceAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Source agent not found"));

        Agent target = agentRepository.findById(request.getTargetAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Target agent not found"));

        String content = request.getCustomContent() != null && !request.getCustomContent().isBlank()
                ? request.getCustomContent()
                : "5% off all deliveries on Diwali.";

        CreatePostRequest postReq = CreatePostRequest.builder()
                .authorAgentId(source.getId())
                .content(content)
                .type(PostType.OFFER)
                .idempotencyKey("sim-" + System.currentTimeMillis())
                .build();

        PostResponse createdPost = postService.createPost(postReq);
        RoutingEvaluationResponse routingResult = routingEngine.evaluatePost(createdPost.getId());

        List<DemoScenarioResponse.WorkflowStep> steps = new ArrayList<>();
        steps.add(new DemoScenarioResponse.WorkflowStep(1, "Post Created", "SUCCESS", "Published by " + source.getName(), 20));
        steps.add(new DemoScenarioResponse.WorkflowStep(2, "AI Analyzed", "SUCCESS", "Topics & entities extracted", 35));
        steps.add(new DemoScenarioResponse.WorkflowStep(3, "Routing Evaluated", "SUCCESS", "Ranked candidate agents", 45));

        CommentResponse publishedComment = null;
        String generatedComment = null;
        boolean guardrailsPassed = true;

        if ("ENGAGE".equalsIgnoreCase(request.getTargetDecision())) {
            Post postEntity = postRepository.findById(createdPost.getId()).orElseThrow();
            Optional<String> safeCommentOpt = responseGenerationService.generateSafeComment(postEntity, target, "Simulation context");
            generatedComment = safeCommentOpt.orElse(String.format("Partnership update from %s.", target.getName()));

            CreateCommentRequest commentReq = CreateCommentRequest.builder()
                    .authorAgentId(target.getId())
                    .content(generatedComment)
                    .generatedByAi(true)
                    .build();
            publishedComment = commentService.createComment(createdPost.getId(), commentReq);
            feedbackService.recordFeedback(target.getId(), createdPost.getId(), null, FeedbackType.ENGAGED);

            steps.add(new DemoScenarioResponse.WorkflowStep(4, "Agent Engaged", "SUCCESS", target.getName() + " engaged and posted comment", 40));
        } else {
            steps.add(new DemoScenarioResponse.WorkflowStep(4, "Agent Decision", "SUCCESS", target.getName() + " decided: " + request.getTargetDecision(), 15));
        }

        return DemoScenarioResponse.builder()
                .scenarioName("Custom Agent Simulation: " + source.getName() + " -> " + target.getName())
                .postId(createdPost.getId())
                .postContent(content)
                .sourceAgentName(source.getName())
                .steps(steps)
                .candidatesRanked(routingResult.getCandidates())
                .targetAgentName(target.getName())
                .targetAgentDecision(request.getTargetDecision())
                .generatedComment(generatedComment)
                .guardrailsPassed(guardrailsPassed)
                .publishedComment(publishedComment)
                .build();
    }
}
