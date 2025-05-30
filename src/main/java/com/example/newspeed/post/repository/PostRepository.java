package com.example.newspeed.post.repository;

import com.example.newspeed.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByCreatedAtBetweenOrderByModifiedAtDesc(LocalDateTime start, LocalDateTime end);

    // 게시글 전체 조회 기능 페이징처리
    Page<Post> findAll(Pageable pageable);

}
