package com.example.newspeed.post.controller;

import com.example.newspeed.post.dto.PostLikesDto;
import com.example.newspeed.post.service.PostLikesService;
import com.example.newspeed.auth.dto.CustomUserDetails;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostLikesController {
    private final PostLikesService postLikesService;

    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> addLikes(@PathVariable @Min(1) Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();

        postLikesService.addLikes(new PostLikesDto(userId, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> deleteLikes(@PathVariable @Min(1) Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();

        postLikesService.deleteLikes(new PostLikesDto(userId, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
