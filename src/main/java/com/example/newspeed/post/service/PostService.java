package com.example.newspeed.post.service;

import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.post.dto.FindAllPostResponseDto;
import com.example.newspeed.post.dto.FindOnePostResponseDto;
import com.example.newspeed.post.dto.PostResponseDto;
import com.example.newspeed.post.dto.UpdatePostResponseDto;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.PostRepository;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.service.UserService;
import jakarta.transaction.Transactional;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final PasswordManager passwordManager;

    public PostService(PostRepository postRepository, UserService userService, PasswordManager passwordManager) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.passwordManager = passwordManager;
    }

    @Transactional
    public PostResponseDto createPost(Long currentUserId, String title, String contents, String imageUrl) {

        User user = userService.findUserById(currentUserId);

        Post newPost = new Post(user, title, contents, imageUrl);

        // 게시글 생성 시 제목이 비어있을 시 예외처리
        if (newPost.getTitle() == null) {
            throw new CustomException(ErrorCode.POST_NOT_TITLE);
        }

        // 게시글 생성 시 내용이 비어있을 시 예외처리
        if (newPost.getContents() == null) {
            throw new CustomException(ErrorCode.POST_NOT_CONTENTS);
        }

        // 게시글 생성 시 이미지가 비어있을 시 예외처리
        if (newPost.getImageUrl() == null) {
            throw new CustomException(ErrorCode.POST_NOT_IMAGE);
        }

        // 게시글 생성 시 제목의 길이가 255자를 초과할 경우 예외처리
        if (newPost.getTitle().length() > 255) {
            throw new CustomException(ErrorCode.TITLE_LENGTH_OVER);
        }

        Post savePost = postRepository.save(newPost);

        PostResponseDto responseDto = new PostResponseDto
                (
                        savePost.getId(),
                        savePost.getUser().getId(),
                        savePost.getTitle(),
                        savePost.getContents(),
                        savePost.getImageUrl(),
                        savePost.getUser().getUserUrl(),
                        savePost.getPostLikes().size(),
                        savePost.getComments().size(),
                        savePost.getCreatedAt(),
                        savePost.getModifiedAt()
                );

        return responseDto;
    }

    @Transactional
    public List<FindAllPostResponseDto> findAllPost(int page, int size) {

        // 페이지 번호 1을 -> 0으로 변경하여 파라미터에 1입력 시 0번째 페이지 조회
        page -= 1;

        // 페이지 번호, 크기 지정 및 정렬 (생성 일자 기준으로 내림차순)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 레포지토리에서 생성한 게시글 전체 조회 페이징 기능
        Page<Post> postPage = postRepository.findAll(pageable);

        // 리스트로 반환
        return postPage.stream().map(FindAllPostResponseDto::toPostDto).toList();

    }


    // 게시글 단건 조회 기능
    @Transactional
    public FindOnePostResponseDto findOnePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        FindOnePostResponseDto responseDto = new FindOnePostResponseDto(
                id,
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContents(),
                post.getImageUrl(),
                post.getUserUrl(),
                post.getPostLikes().size(),
                post.getComments().size(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );

        return responseDto;
    }

    public List<FindAllPostResponseDto> findPostsByPeriod(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay(); // 해당일 00:00:00
        LocalDateTime end = endDate.atTime(LocalTime.MAX); // 해당일 23:59:59까지 포함

        List<Post> posts = postRepository.findAllByCreatedAtBetweenOrderByModifiedAtDesc(start, end);

        return posts.stream().map(FindAllPostResponseDto::toPostDto).toList();
    }


    @Transactional
    public Post findPostById(Long id) {
        Optional<Post> findPost = postRepository.findById(id);
        return findPost.get();
    }


    // Post 게시글 수정 기능
    @Transactional
    public UpdatePostResponseDto updatedPost(Long id, Long currentUserId, String title, String contents, String imageUrl) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));


        if (currentUserId.equals(post.getUser().getId())) {
            if (title.equals(post.getTitle()) && contents.equals(post.getContents()) && imageUrl.equals(post.getImageUrl())) {
                throw new CustomException(ErrorCode.POST_NOT_CHANGE);
            }

            if (title != null) {
                post.setTitle(title);
            }

            if (contents != null) {
                post.setContents(contents);
            }

            if (imageUrl != null) {
                post.setImageUrl(imageUrl);
            }
        } else {
            throw new CustomException(ErrorCode.POST_NOT_OWNED);
        }

        UpdatePostResponseDto responseDto = new UpdatePostResponseDto(
                post.getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContents(),
                post.getImageUrl(),
                post.getUserUrl(),
                post.getPostLikes().size(),
                post.getComments().size(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );

        return responseDto;
    }

    // 게시글 삭제 기능
    @Transactional
    public void deletePost(Long id, Long currentUserId, String password) {

        // 입력받은 아이디로 Post 불러오기
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (currentUserId.equals(post.getUser().getId())) {
            // 입력한 비밀번호와 유저 비밀번호가 같은지 검증
            passwordManager.validatePasswordMatchOrThrow(password, post.getUser().getPassword());

            postRepository.delete(post);
        } else {
            throw new CustomException(ErrorCode.POST_NOT_OWNED);
        }
    }
}

