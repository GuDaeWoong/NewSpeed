package com.example.newspeed.comment.controller;


import com.example.newspeed.comment.dto.*;
import com.example.newspeed.comment.service.CommentService;
import com.example.newspeed.global.dto.PageResponseDto;
import com.example.newspeed.user.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
        Long currentUserId = userDetails.getId();
        return new ResponseEntity<>(commentService.saveComment(postId, requestDto, currentUserId), HttpStatus.CREATED);
    }

    // post 선택 하여 모든 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PageResponseDto<CommentWithLikesDto>> findAllCommentByPostId(
            @PathVariable Long postId,
            @PageableDefault(page = 1, sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CommentWithLikesDto> commentPage = commentService.findAllCommentByPostId(postId, pageable);
        return new ResponseEntity<>(new PageResponseDto<>(commentPage), HttpStatus.OK);
    }

    // 댓글 업데이트
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentUpdateResponseDto> updateComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                  @PathVariable Long commentId,
                                                                  @Valid @RequestBody CommentRequestDto commentRequestDto
    ) {
        Long currentUserId = userDetails.getId();
        CommentUpdateResponseDto commentUpdate = commentService.updateComment(commentId, commentRequestDto, currentUserId);
        return new ResponseEntity<>(commentUpdate, HttpStatus.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long commentId,
                                              @RequestBody DeleteCommentDto deleteDto
    ) {
        Long currentUserId = userDetails.getId();
        commentService.deleteComment(commentId, deleteDto, currentUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
