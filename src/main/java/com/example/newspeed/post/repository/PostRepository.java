package com.example.newspeed.post.repository;

import com.example.newspeed.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시글 조회 -> 기간 검색 기능 페이징 처리
    Page<Post> findAllByCreatedAtBetweenOrderByModifiedAtDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 게시글 전체 조회 기능 페이징처리
    Page<Post> findAll(Pageable pageable);

}
