package com.example.newspeed.post.service;

import com.example.newspeed.post.dto.FindPostResponseDto;
import com.example.newspeed.post.dto.PostResponseDto;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.PostRepository;
import jakarta.transaction.Transactional;

import com.example.newspeed.post.dto.PostResponseDto;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponseDto createPost(String title, String content, String imageUrl) {

        Post newPost = new Post(title, content, imageUrl);

        Post savePost = postRepository.save(newPost);

        PostResponseDto responseDto = new PostResponseDto
                (
                        savePost.getId(),
                        savePost.getUser().getId(),
                        // 작성자 닉네임
                        // 본인 프로필파일 url
                        savePost.getTitle(),
                        savePost.getContents(),
                        savePost.getImageUrl(),
                        savePost.getCreatedAt(),
                        savePost.getModifiedAt()
                );

        return responseDto;
    }

    @Transactional
    public List<FindPostResponseDto> findAllPost() {

        return postRepository.findAll().stream().map(FindPostResponseDto::toPostDto).toList();
    }

}
