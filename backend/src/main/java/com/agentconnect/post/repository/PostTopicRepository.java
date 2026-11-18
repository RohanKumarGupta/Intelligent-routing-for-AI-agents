package com.agentconnect.post.repository;

import com.agentconnect.post.model.PostTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostTopicRepository extends JpaRepository<PostTopic, UUID> {

    List<PostTopic> findByPostId(UUID postId);

    List<PostTopic> findByTopic(String topic);
}
