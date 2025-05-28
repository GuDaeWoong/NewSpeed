package com.example.newspeed.post.service;

import com.example.newspeed.post.dto.FindPostResponseDto;
import com.example.newspeed.post.dto.PostResponseDto;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    // postId를 입력받아 Post객체로 반환
    @Transactional
    public Post findPostById(Long id) {
        Optional<Post> findById = postRepository.findById(id);
        return findById.get();
    }
}
