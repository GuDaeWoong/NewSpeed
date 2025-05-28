package com.example.newspeed.comment.controller;


import com.example.newspeed.comment.dto.CommentRequestDto;
import com.example.newspeed.comment.dto.CommentResponseDto;
import com.example.newspeed.comment.service.CommentService;
import com.example.newspeed.global.common.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponseDto> saveComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto
    ) {
        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();
        return new ResponseEntity<>(commentService.saveComment(postId, requestDto, currentUserId), HttpStatus.CREATED);
    }

    // post 선택 하여 모든 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponseDto>> findAllCommentByPostId(@PathVariable Long postId
    ) {
        return new ResponseEntity<>(commentService.findAllCommentByPostId(postId), HttpStatus.OK);
    }

    // 댓글 업데이트
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> updateCommnet(@PathVariable Long commentId,
                                              @Valid @RequestBody CommentRequestDto commentRequestDto
    ) {
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();
        commentService.updateCommnet(commentId, commentRequestDto, currentUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}
