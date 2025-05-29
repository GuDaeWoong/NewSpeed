package com.example.newspeed.comment.controller;

import com.example.newspeed.comment.dto.CommentLikesDto;
import com.example.newspeed.comment.service.CommentLikesService;
import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.post.dto.PostLikesDto;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentLikesController {
    private final CommentLikesService commentLikesService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likes(@PathVariable @Min(1) Long id){
        Long userId = jwtTokenProvider.getUserIdFromSecurity();

        commentLikesService.likes(new CommentLikesDto(userId, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
