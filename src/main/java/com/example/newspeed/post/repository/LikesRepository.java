package com.example.newspeed.post.repository;

import com.example.newspeed.post.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserIdAndPostId(Long userId, Long postId);
}
