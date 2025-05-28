package com.example.newspeed.post.service;

import com.example.newspeed.post.dto.FindPostResponseDto;
import com.example.newspeed.post.dto.PostResponseDto;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.PostRepository;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.service.UserService;
import jakarta.transaction.Transactional;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository,UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Transactional
    public PostResponseDto createPost(String title, String content, String imageUrl, Long currentUserId) {

        User user = userService.findUserById(currentUserId);

        Post newPost = new Post(user, title, content, imageUrl);

        Post savePost = postRepository.save(newPost);

        PostResponseDto responseDto = new PostResponseDto
                (
                        savePost.getId(),
                        savePost.getUser().getId(),
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


    // 게시글 단건 조회 기능
    @Transactional
    public FindPostResponseDto findOnePost(Long id) {

        // 레포지토리에서 생성한 기능을 사용
        Post post = postRepository.findByIdOrElseThrow(id);

        FindPostResponseDto responseDto = new FindPostResponseDto(
                id,
                "닉네임", // post.getUser().getNickname,
                post.getTitle(),
                post.getContents(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );

        return responseDto;
    }

    @Transactional
    public Post findPostById(Long id) {
        Optional<Post> findPost = postRepository.findById(id);
        return findPost.get();
    }


    // Post 게시글 수정 기능
    @Transactional
    public FindPostResponseDto updatedPost(Long id, String title, String contents, String imageUrl) {

        Optional<Post> findById = postRepository.findById(id);
        Post post = findById.get();

        if (title != null) {
            post.setTitle(title);
        }

        if (contents != null) {
            post.setContents(contents);
        }

        if (imageUrl != null) {
            post.setImageUrl(imageUrl);
        }

        FindPostResponseDto responseDto = new FindPostResponseDto(
                post.getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContents(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );

        return responseDto;
    }
}

