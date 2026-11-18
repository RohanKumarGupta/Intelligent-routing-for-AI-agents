package com.agentconnect.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic postCreatedTopic() {
        return TopicBuilder.name("post-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic postAnalyzedTopic() {
        return TopicBuilder.name("post-analyzed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic routingCompletedTopic() {
        return TopicBuilder.name("routing-completed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationCreatedTopic() {
        return TopicBuilder.name("notification-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic agentDecisionTopic() {
        return TopicBuilder.name("agent-decision")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic commentCreatedTopic() {
        return TopicBuilder.name("comment-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic feedbackCreatedTopic() {
        return TopicBuilder.name("feedback-created")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
