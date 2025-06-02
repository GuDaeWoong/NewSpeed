package com.example.newspeed.post.repository;

import com.example.newspeed.post.entity.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikesRepository extends JpaRepository<PostLikes, Long> {

    Optional<PostLikes> findByUserIdAndPostPostId(Long userId, Long postId);
}