package com.example.newspeed.post.service;

import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.global.common.SecurityConfig;
import com.example.newspeed.post.dto.FindAllPostResponseDto;
import com.example.newspeed.post.dto.FindOnePostResponseDto;
import com.example.newspeed.post.dto.PostResponseDto;
import com.example.newspeed.post.dto.UpdatePostResponseDto;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.PostRepository;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.service.UserService;
import jakarta.transaction.Transactional;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final SecurityConfig securityConfig;
    private final PasswordManager passwordManager;

    public PostService(PostRepository postRepository, UserService userService, SecurityConfig securityConfig, PasswordManager passwordManager) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.securityConfig = securityConfig;
        this.passwordManager = passwordManager;
    }

    @Transactional
    public PostResponseDto createPost(Long currentUserId, String title, String contents, String imageUrl) {

        User user = userService.findUserById(currentUserId);

        Post newPost = new Post(user, title, contents, imageUrl);

        Post savePost = postRepository.save(newPost);

        PostResponseDto responseDto = new PostResponseDto
                (
                        savePost.getId(),
                        savePost.getUser().getId(),
                        savePost.getTitle(),
                        savePost.getContents(),
                        savePost.getImageUrl(),
                        savePost.getUser().getUserUrl(),
                        savePost.getCreatedAt(),
                        savePost.getModifiedAt()
                );

        return responseDto;
    }

    @Transactional
    public List<FindAllPostResponseDto> findAllPost() {

        return postRepository.findAll().stream().map(FindAllPostResponseDto::toPostDto).toList();
    }


    // 게시글 단건 조회 기능
    @Transactional
    public FindOnePostResponseDto findOnePost(Long id) {

        // 레포지토리에서 생성한 기능을 사용
        Post post = postRepository.findByIdOrElseThrow(id);

        FindOnePostResponseDto responseDto = new FindOnePostResponseDto(
                id,
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContents(),
                post.getImageUrl(),
                post.getUserUrl(),
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
    public UpdatePostResponseDto updatedPost(Long id, Long currentUserId, String title, String contents, String imageUrl) {

        Optional<Post> findById = postRepository.findById(id);
        Post post = findById.get();

        if (currentUserId.equals(post.getUser().getId())) {
            if (title != null) {
                post.setTitle(title);
            }

            if (contents != null) {
                post.setContents(contents);
            }

            if (imageUrl != null) {
                post.setImageUrl(imageUrl);
            }
        }
        UpdatePostResponseDto responseDto = new UpdatePostResponseDto(
                post.getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContents(),
                post.getImageUrl(),
                post.getUserUrl(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );

        return responseDto;
    }

    // 게시글 삭제 기능
    @Transactional
    public void deletePost(Long id, Long currentUserId, String password) {

        // 입력받은 아이디로 Post 불러오기
        Post post = postRepository.findByIdOrElseThrow(id);

        if (currentUserId.equals(post.getUser().getId())) {
            // 입력한 비밀번호와 유저 비밀번호가 같은지 검증
            passwordManager.validatePasswordMatchOrThrow(password, post.getUser().getPassword());

            postRepository.delete(post);

        }
    }
}

