package com.example.newspeed.comment.controller;


import com.example.newspeed.comment.dto.CommentRequestDto;
import com.example.newspeed.comment.dto.CommentResponseDto;
import com.example.newspeed.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

//    @PostMapping
//    public ResponseEntity<CommentResponseDto> saveComment(
//            @PathVariable Long postId,
//            @RequestBody CommentRequestDto requestDto,
//            // 로그인한 유저정보
//    ) {
//        // 로그인 유저 id
////        Long currentUserId = userDetails.getId();
//        return new ResponseEntity<>(commentService.saveComment(postId, requestDto, requestDto, currentUserId), HttpStatus.CREATED);
//    }

    // post 선택 하여 모든 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponseDto>> findAllCommentByPostId(@PathVariable Long postId
    ) {
        return new ResponseEntity<>(commentService.findAllCommentByPostId(postId), HttpStatus.OK);
    }

}
