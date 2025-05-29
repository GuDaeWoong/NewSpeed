package com.example.newspeed.post.repository;

import com.example.newspeed.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Optional 처리를 위해 Repository에 default 기능 생성
    default Post findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    List<Post> findAllByCreatedAtBetweenOrderByModifiedAtDesc(LocalDateTime start, LocalDateTime end);
    // 게시글 전체 조회 기능 페이징처리
    Page<Post> findAll(Pageable pageable);

}
