package com.example.newspeed.post.controller;

import com.example.newspeed.post.dto.PostRequestDto;
import com.example.newspeed.post.dto.PostResponseDto;
import com.example.newspeed.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    @PostMapping
    public ResponseEntity<PostResponseDto> createPostAPI(@RequestBody PostRequestDto dto) {

        PostResponseDto responseDto = postService.createPost(dto.getTitle(), dto.getContents(), dto.getImageUrl());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

}
