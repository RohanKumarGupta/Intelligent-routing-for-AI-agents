package com.agentconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.agentconnect.config.RoutingProperties;
import com.agentconnect.config.RateLimitProperties;
import com.agentconnect.config.AiProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableConfigurationProperties({RoutingProperties.class, RateLimitProperties.class, AiProperties.class})
public class AgentConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentConnectApplication.class, args);
    }
}
