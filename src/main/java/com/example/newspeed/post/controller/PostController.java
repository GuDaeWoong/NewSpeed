package com.example.newspeed.post.controller;


import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.post.dto.*;
import com.example.newspeed.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;

    public PostController(PostService postService, JwtTokenProvider jwtTokenProvider) {
        this.postService = postService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostMapping
    public ResponseEntity<PostResponseDto> createPostAPI(@RequestBody PostRequestDto dto) {
        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();

        PostResponseDto responseDto = postService.createPost(currentUserId, dto.getTitle(), dto.getContents(), dto.getImageUrl());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FindAllPostResponseDto>> findAllAPI() {

        List<FindAllPostResponseDto> allPost = postService.findAllPost();

        return new ResponseEntity<>(allPost, HttpStatus.OK);
    }

    // 게시글 단건 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<FindAllPostResponseDto> findOneAPI(@PathVariable Long id) {

        FindAllPostResponseDto findOnePost = postService.findOnePost(id);

        return new ResponseEntity<>(findOnePost, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<FindAllPostResponseDto>> searchPostByPeriod(@RequestBody SearchPeriodRequestDto requestDto) {
        List<FindAllPostResponseDto> searchPost = postService.findPostsByPeriod(requestDto.getStartDate(), requestDto.getEndDate());

        return new ResponseEntity<>(searchPost, HttpStatus.OK);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<FindAllPostResponseDto> updatedPostAPI(@PathVariable Long id, @RequestBody PostRequestDto dto) {

        FindAllPostResponseDto responseDto = postService.updatedPost(id, dto.getTitle(), dto.getContents(), dto.getImageUrl());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostAPI(@PathVariable Long id, @RequestBody DeletePostRequestDto dto) {

        postService.deletePost(id, dto.getPassword());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
