package com.agentconnect.post.repository;

import com.agentconnect.post.model.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, UUID> {

    List<PostEntity> findByPostId(UUID postId);
}
