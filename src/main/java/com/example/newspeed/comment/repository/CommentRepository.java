package com.example.newspeed.comment.repository;

import com.example.newspeed.comment.dto.CommentWithLikesDto;
import com.example.newspeed.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findByPostId(Long postId);

    Page<Comment> findByPostId(Long postId, Pageable pageable);
    @Query("SELECT NEW com.example.newspeed.comment.dto.CommentWithLikesDto(" +
            "c.id, " +
            "c.user.id, " +
            "c.user.nickname, " +
            "c.user.userUrl, " +
            "c.post.id, " +
            "c.contents, " +
            "COUNT(cl.id), " +    //commentId 카운트
            "c.createdAt, " +
            "c.modifiedAt) " +
            "FROM Comment c " +
            "LEFT JOIN CommentLikes cl ON c.id = cl.comment.id " +
            "WHERE c.post.id = :postId " + // postId 기준
            "GROUP BY c.id, c.user.id, c.user.nickname, c.user.userUrl, c.post.id, " +
            "c.contents, c.createdAt, c.modifiedAt " +
            "ORDER BY c.modifiedAt DESC")
    Page<CommentWithLikesDto> findCommentsWithLikeCountByPostId(Long postId, Pageable pageable);
}
