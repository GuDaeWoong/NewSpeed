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
    public ResponseEntity<FindOnePostResponseDto> findOneAPI(@PathVariable Long id) {

        FindOnePostResponseDto findOnePost = postService.findOnePost(id);

        return new ResponseEntity<>(findOnePost, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdatePostResponseDto> updatedPostAPI(@PathVariable Long id, @RequestBody PostRequestDto dto) {

        UpdatePostResponseDto responseDto = postService.updatedPost(id, dto.getTitle(), dto.getContents(), dto.getImageUrl());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostAPI(@PathVariable Long id, @RequestBody DeletePostRequestDto dto) {

        postService.deletePost(id, dto.getPassword());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
