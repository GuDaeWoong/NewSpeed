package com.example.newspeed.post.controller;


import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.post.dto.FindPostResponseDto;
import com.example.newspeed.post.dto.PostRequestDto;
import com.example.newspeed.post.dto.PostResponseDto;
import com.example.newspeed.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/posts")
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

        PostResponseDto responseDto = postService.createPost(dto.getTitle(), dto.getContents(), dto.getImageUrl(),currentUserId);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FindPostResponseDto>> findAllAPI() {

        List<FindPostResponseDto> allPost = postService.findAllPost();

        return new ResponseEntity<>(allPost, HttpStatus.OK);
    }

    // 게시글 단건 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<FindPostResponseDto> findOneAPI(@PathVariable Long id) {

        FindPostResponseDto findOnePost = postService.findOnePost(id);

        return new ResponseEntity<>(findOnePost, HttpStatus.OK);
    }


}
