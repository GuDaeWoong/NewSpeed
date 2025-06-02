package com.example.newspeed.post.controller;
import com.example.newspeed.global.dto.PageResponseDto;
import com.example.newspeed.post.dto.*;
import com.example.newspeed.post.service.PostService;
import com.example.newspeed.auth.dto.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    @PostMapping
    public ResponseEntity<PostWithIdResponseDto> createPost(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody PostRequestDto dto) {

        Long currentUserId = userDetails.getId();

        PostWithIdResponseDto responseDto = postService.createPost(currentUserId, dto.getTitle(), dto.getContents(), dto.getImageUrl());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<PostListResponseDto>> findAllPosts(
            @PageableDefault(page = 1, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    )
    {
        PageResponseDto<PostListResponseDto> allPost = postService.findAllPosts(pageable);

        return new ResponseEntity<>(allPost, HttpStatus.OK);
    }

    // 게시글 단건 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<PostWithNickResponseDto> findOnePost(@PathVariable Long id) {

        PostWithNickResponseDto findOnePost = postService.findOnePost(id);

        return new ResponseEntity<>(findOnePost, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponseDto<PostListResponseDto>> findPostsByPeriod(
            @RequestBody PostPeriodRequestDto requestDto,
            @PageableDefault(page = 1, sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponseDto<PostListResponseDto> searchPost =
                postService.findPostsByPeriod(requestDto.getStartDate(), requestDto.getEndDate(), pageable);

        return new ResponseEntity<>(searchPost, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostUpdateResponseDto> updatePost(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @RequestBody PostRequestDto dto) {

        Long currentUserId = userDetails.getId();

        PostUpdateResponseDto responseDto = postService.updatedPost(id, currentUserId, dto.getTitle(), dto.getContents(), dto.getImageUrl());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @RequestBody PostDeleteRequestDto dto) {

        Long currentUserId = userDetails.getId();

        postService.deletePost(id, currentUserId, dto.getPassword());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
