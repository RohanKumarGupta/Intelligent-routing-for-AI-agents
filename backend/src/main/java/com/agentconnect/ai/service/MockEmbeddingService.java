package com.agentconnect.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@Service
public class MockEmbeddingService implements EmbeddingService {

    private static final int DIMENSIONS = 768;

    // Domain clusters mapped to specific vector subspace regions
    private static final Map<String, Integer> DOMAIN_BUCKETS = new HashMap<>();

    static {
        // Cluster 1: Food & Grocery & Delivery (Dimensions 0-99)
        assignCluster(new String[]{"food", "dining", "delivery", "swiggy", "zomato", "restaurant", "meal", "groceries", "instamart", "blinkit"}, 0);

        // Cluster 2: Payments & UPI & Fintech & Banking (Dimensions 100-199)
        assignCluster(new String[]{"payment", "payments", "upi", "phonepe", "paytm", "hdfc", "bank", "card", "credit", "cashback", "wallet", "fintech", "visa", "mastercard"}, 100);

        // Cluster 3: E-commerce & Retail & Offers & Festivals (Dimensions 200-299)
        assignCluster(new String[]{"ecommerce", "retail", "offer", "offers", "discount", "discounts", "sale", "diwali", "festive", "flipkart", "amazon", "deal"}, 200);

        // Cluster 4: Travel & Tourism (Dimensions 300-399)
        assignCluster(new String[]{"travel", "flight", "hotel", "holiday", "makemytrip", "tourism", "vacation", "trip"}, 300);

        // Cluster 5: Automotive & Mobility (Dimensions 400-499)
        assignCluster(new String[]{"automotive", "motorcycle", "vehicle", "manufacturing", "mobility", "bajaj", "bike", "car", "engine"}, 400);

        // Cluster 6: Tech & Cloud (Dimensions 500-599)
        assignCluster(new String[]{"cloud", "technology", "software", "api", "infra", "ai"}, 500);
    }

    private static void assignCluster(String[] words, int startOffset) {
        for (String word : words) {
            DOMAIN_BUCKETS.put(word.toLowerCase(), startOffset);
        }
    }

    @Override
    public float[] generateEmbedding(String text) {
        float[] vector = new float[DIMENSIONS];
        if (text == null || text.isBlank()) {
            return vector;
        }

        String lower = text.toLowerCase();
        String[] tokens = lower.split("[^a-zA-Z0-9]+");

        // Base background noise using deterministic pseudo-random seed from text hash
        Random pseudoRandom = new Random(deterministicSeed(text));
        for (int i = 0; i < DIMENSIONS; i++) {
            vector[i] = (float) (pseudoRandom.nextGaussian() * 0.02);
        }

        // Activate specific cluster subspace features
        for (String token : tokens) {
            for (Map.Entry<String, Integer> entry : DOMAIN_BUCKETS.entrySet()) {
                if (token.contains(entry.getKey()) || entry.getKey().contains(token)) {
                    int offset = entry.getValue();
                    for (int j = 0; j < 50; j++) {
                        int index = (offset + j) % DIMENSIONS;
                        vector[index] += 0.8f;
                    }
                    // Cross-domain coupling between Commerce, Food, and Payments
                    if (offset == 0 || offset == 100 || offset == 200) {
                        for (int k = 600; k < 680; k++) {
                            vector[k] += 0.45f;
                        }
                    }
                }
            }
        }

        // Normalize to unit L2 norm
        normalize(vector);
        return vector;
    }

    @Override
    public List<float[]> generateEmbeddings(List<String> texts) {
        List<float[]> results = new ArrayList<>();
        for (String text : texts) {
            results.add(generateEmbedding(text));
        }
        return results;
    }

    private long deterministicSeed(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            long seed = 0;
            for (int i = 0; i < 8; i++) {
                seed = (seed << 8) | (digest[i] & 0xFF);
            }
            return seed;
        } catch (Exception e) {
            return text.hashCode();
        }
    }

    private void normalize(float[] v) {
        float sumSq = 0;
        for (float val : v) {
            sumSq += val * val;
        }
        if (sumSq > 0) {
            float norm = (float) Math.sqrt(sumSq);
            for (int i = 0; i < v.length; i++) {
                v[i] /= norm;
            }
        }
    }
}
