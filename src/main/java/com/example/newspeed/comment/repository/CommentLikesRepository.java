package com.example.newspeed.comment.repository;

import com.example.newspeed.comment.entity.CommentLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {

    Optional<CommentLikes> findByUserIdAndCommentId(Long userId, Long commentId);
}
