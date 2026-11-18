package com.agentconnect.ai.service;

import com.agentconnect.ai.dto.ContentAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class MockLLMService implements LLMService {

    @Override
    public ContentAnalysisResult analyzeContent(String content) {
        String lower = content.toLowerCase();
        Set<String> topics = new LinkedHashSet<>();
        List<ContentAnalysisResult.EntityInfo> entities = new ArrayList<>();
        String intent = "GENERAL_ANNOUNCEMENT";
        String geography = "IN";

        // Topic detection
        if (lower.contains("deliver") || lower.contains("food") || lower.contains("order") || lower.contains("restaurant") || lower.contains("meal")) {
            topics.add("food_delivery");
            topics.add("commerce");
        }
        if (lower.contains("off") || lower.contains("discount") || lower.contains("cashback") || lower.contains("save") || lower.contains("deal") || lower.contains("%")) {
            topics.add("discounts");
            topics.add("offers");
            intent = "PROMOTION";
        }
        if (lower.contains("diwali") || lower.contains("festiv") || lower.contains("holiday") || lower.contains("new year")) {
            topics.add("festivals");
            topics.add("diwali");
        }
        if (lower.contains("upi") || lower.contains("pay") || lower.contains("card") || lower.contains("bank")) {
            topics.add("digital_payments");
            topics.add("fintech");
        }
        if (lower.contains("bike") || lower.contains("car") || lower.contains("motorcycle") || lower.contains("ev") || lower.contains("auto")) {
            topics.add("automotive");
            topics.add("mobility");
        }
        if (lower.contains("travel") || lower.contains("flight") || lower.contains("hotel")) {
            topics.add("travel");
        }

        if (topics.isEmpty()) {
            topics.add("general");
        }

        // Entity detection
        if (lower.contains("swiggy")) {
            entities.add(new ContentAnalysisResult.EntityInfo("Swiggy", "ORGANIZATION", 0.99));
        }
        if (lower.contains("phonepe")) {
            entities.add(new ContentAnalysisResult.EntityInfo("PhonePe", "ORGANIZATION", 0.99));
        }
        if (lower.contains("hdfc")) {
            entities.add(new ContentAnalysisResult.EntityInfo("HDFC Bank", "ORGANIZATION", 0.98));
        }
        if (lower.contains("zomato")) {
            entities.add(new ContentAnalysisResult.EntityInfo("Zomato", "ORGANIZATION", 0.98));
        }
        if (lower.contains("diwali")) {
            entities.add(new ContentAnalysisResult.EntityInfo("Diwali", "EVENT", 0.95));
        }
        if (lower.contains("5%")) {
            entities.add(new ContentAnalysisResult.EntityInfo("5%", "PERCENTAGE", 0.90));
        }

        return ContentAnalysisResult.builder()
                .topics(new ArrayList<>(topics))
                .entities(entities)
                .intent(intent)
                .geography(geography)
                .build();
    }

    @Override
    public String generateResponse(String systemPrompt, String userPrompt) {
        return "Acknowledged: response processed according to agent policy.";
    }

    @Override
    public String generateAgentEngagementComment(String postContent, String agentName, String companyName, String capabilities, String businessContext) {
        String lowerPost = postContent.toLowerCase();
        String lowerCompany = companyName.toLowerCase();

        if (lowerCompany.contains("phonepe")) {
            if (lowerPost.contains("swiggy") || lowerPost.contains("deliver") || lowerPost.contains("diwali")) {
                return "Extra ₹50 cashback when you pay for eligible Swiggy orders with PhonePe UPI on Diwali.";
            }
            return "Instant UPI payments and flat cashback unlocked for your customers on this campaign.";
        }

        if (lowerCompany.contains("hdfc")) {
            return "Get an additional 10% instant discount up to ₹150 using HDFC Bank Credit & Debit cards on this festive offer.";
        }

        if (lowerCompany.contains("paytm")) {
            return "Pay with Paytm UPI to earn double cashback points and exclusive scratch card vouchers!";
        }

        if (lowerCompany.contains("flipkart")) {
            return "Excited to partner on cross-platform festive deals during this celebration season!";
        }

        return String.format("%s (%s) is happy to collaborate on this initiative.", agentName, companyName);
    }
}
