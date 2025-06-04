package com.example.newspeed.comment.controller;

import com.example.newspeed.comment.dto.CommentLikesDto;
import com.example.newspeed.comment.service.CommentLikesService;
import com.example.newspeed.auth.dto.CustomUserDetails;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentLikesController {
    private final CommentLikesService commentLikesService;

    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> addLikes(@PathVariable @Min(1) Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();

        commentLikesService.addLikes(new CommentLikesDto(userId, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> deleteLikes(@PathVariable @Min(1) Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();

        commentLikesService.deleteLikes(new CommentLikesDto(userId, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
