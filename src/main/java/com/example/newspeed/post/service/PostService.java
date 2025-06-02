package com.example.newspeed.post.service;

import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.common.PasswordManager;

import com.example.newspeed.global.dto.PageResponseDto;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.post.dto.*;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.PostRepository;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.service.UserService;
import jakarta.transaction.Transactional;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    public PostWithIdResponseDto createPost(Long currentUserId, String title, String contents, String imageUrl) {

        User user = userService.findUserById(currentUserId);

        Post newPost = new Post(user, title, contents, imageUrl);

        // 게시글 생성 시 제목이 비어있을 시 예외처리
        if (newPost.getTitle() == null || newPost.getTitle().trim().isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_TITLE);
        }

        // 게시글 생성 시 내용이 비어있을 시 예외처리
        if (newPost.getContents() == null || newPost.getContents().trim().isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_CONTENTS);
        }

        // 게시글 생성 시 이미지가 비어있을 시 예외처리
        if (newPost.getImageUrl() == null || newPost.getImageUrl().trim().isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_IMAGE);
        }

        // 게시글 생성 시 제목의 길이가 255자를 초과할 경우 예외처리
        if (newPost.getTitle().length() > 255) {
            throw new CustomException(ErrorCode.TITLE_LENGTH_OVER);
        }

        Post savePost = postRepository.save(newPost);

        return PostWithIdResponseDto.toDto(savePost);
    }

    @Transactional
    public PageResponseDto<PostListResponseDto> findAllPosts(Pageable pageable) {

        // Pageable의 페이지 번호가 1부터 시작한다고 가정하고, 0부터 시작하는 Pageable로 변환
        int pageNumber = pageable.getPageNumber()-1 ;
        // 요청 페이지 번호가 음수가 되는 것을 방지
        if (pageNumber < 0) {
            pageNumber = 0;
        }

        // Pageble객체 생성
        Pageable postPageble = PageRequest.of(pageNumber , pageable.getPageSize(), pageable.getSort());
        // 레포지토리에서 생성한 게시글 전체 조회 페이징 기능
        Page<Post> postPage = postRepository.findAll(postPageble);

        if (postPage.isEmpty() && pageNumber >= postPage.getTotalPages()) {
            throw new CustomException(ErrorCode.PAGE_NOT_FOUND);
        }
        
        // 페이지 반환
        return new PageResponseDto<>(postPage.map(PostListResponseDto::toPostDto));

    }


    // 게시글 단건 조회 기능
    @Transactional
    public PostWithNickResponseDto findOnePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return PostWithNickResponseDto.toDto(id, post);
    }

    public PageResponseDto<PostListResponseDto> findPostsByPeriod(
            LocalDate startDate, LocalDate endDate, Pageable pageable)
    {
        LocalDateTime start = startDate.atStartOfDay(); // 해당일 00:00:00
        LocalDateTime end = endDate.atTime(LocalTime.MAX); // 해당일 23:59:59까지 포함

        // Pageable 의 페이지 번호가 1부터 시작한다고 가정하고, 0부터 시작하는 Pageable 로 변환
        int pageNumber = pageable.getPageNumber()-1 ;
        // 요청 페이지 번호가 음수가 되는 것을 방지
        if (pageNumber < 0) {
            pageNumber = 0;
        }

        // Pageable 객체 생성 -> 페이지 번호, 크기, 정렬 설정
        Pageable Postpageable = PageRequest.of(pageNumber, pageable.getPageSize(), pageable.getSort());

        // 게시글 조회 -> 페이징 처리
        Page<Post> postPage = postRepository.findAllByCreatedAtBetweenOrderByModifiedAtDesc(start, end, Postpageable);

        return new PageResponseDto<>(postPage.map(PostListResponseDto::toPostDto));
    }


    @Transactional
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }


    // Post 게시글 수정 기능
    @Transactional
    public PostUpdateResponseDto updatedPost(Long id, Long currentUserId, String title, String contents, String imageUrl) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));


        if (currentUserId.equals(post.getUser().getId())) {
            if (title.equals(post.getTitle()) && contents.equals(post.getContents()) && imageUrl.equals(post.getImageUrl())) {
                throw new CustomException(ErrorCode.POST_NOT_CHANGE);
            }

            if (title != null && !title.trim().isEmpty()) {
                post.setTitle(title);
            } else throw new CustomException(ErrorCode.POST_NOT_TITLE);

            if (contents != null && !contents.trim().isEmpty()) {
                post.setContents(contents);
            } else throw new CustomException(ErrorCode.POST_NOT_CONTENTS);

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                post.setImageUrl(imageUrl);
            } else throw new CustomException(ErrorCode.POST_NOT_IMAGE);

        } else {
            throw new CustomException(ErrorCode.POST_NOT_OWNED);
        }

        return PostUpdateResponseDto.toDto(post);
    }

    // 게시글 삭제 기능
    @Transactional
    public void deletePost(Long id, Long currentUserId, String password) {

        // 입력받은 아이디로 Post 불러오기
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (currentUserId.equals(post.getUser().getId())) {
            // 입력한 비밀번호와 유저 비밀번호가 같은지 검증
            passwordManager.validatePasswordMatchOrThrow(password, post.getUser().getPassword());

            postRepository.delete(post);
        } else {
            throw new CustomException(ErrorCode.POST_NOT_OWNED);
        }
    }
}

