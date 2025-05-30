package com.example.newspeed.comment.controller;


import com.example.newspeed.comment.dto.CommentRequestDto;
import com.example.newspeed.comment.dto.CommentResponseDto;
import com.example.newspeed.comment.dto.CommentWithLikesDto;
import com.example.newspeed.comment.dto.DeleteCommentDto;
import com.example.newspeed.comment.service.CommentService;
import com.example.newspeed.user.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponseDto> saveComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto
    ) {
        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = userDetails.getId();
        return new ResponseEntity<>(commentService.saveComment(postId, requestDto, currentUserId), HttpStatus.CREATED);
    }

    // post 선택 하여 모든 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<Page<CommentWithLikesDto>> findAllCommentByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CommentWithLikesDto> commentPage = commentService.findAllCommentByPostId(postId, page, size);

        return new ResponseEntity<>(commentPage, HttpStatus.OK);
    }

    // 댓글 업데이트
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> updateCommnet(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long commentId,
                                              @Valid @RequestBody CommentRequestDto commentRequestDto
    ) {
        Long currentUserId = userDetails.getId();
        commentService.updateCommnet(commentId, commentRequestDto, currentUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long commentId,
                                              @RequestBody DeleteCommentDto deleteDto
    ) {
        Long currentUserId = userDetails.getId();
        commentService.deleteComment(commentId, deleteDto, currentUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}
