package com.example.newspeed.post.service;

import com.example.newspeed.post.dto.FindAllPostResponseDto;
import com.example.newspeed.post.dto.PostResponseDto;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
                        1L, //savePost.getUser().getId(),
                        savePost.getTitle(),
                        savePost.getContents(),
                        savePost.getImageUrl(),
                        savePost.getCreatedAt(),
                        savePost.getModifiedAt()
                );

        return responseDto;
    }

    @Transactional
    public List<FindAllPostResponseDto> findAllPost() {

        return postRepository.findAll().stream().map(FindAllPostResponseDto::toPostDto).toList();
    }
}
