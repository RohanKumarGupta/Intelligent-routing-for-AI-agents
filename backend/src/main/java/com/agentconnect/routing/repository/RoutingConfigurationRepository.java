package com.agentconnect.routing.repository;

import com.agentconnect.routing.model.RoutingConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoutingConfigurationRepository extends JpaRepository<RoutingConfiguration, UUID> {
    Optional<RoutingConfiguration> findByActiveTrue();
}
