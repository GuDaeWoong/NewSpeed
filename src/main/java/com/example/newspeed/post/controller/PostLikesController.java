package com.example.newspeed.post.controller;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.post.dto.PostLikesDto;
import com.example.newspeed.post.service.PostLikesService;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostLikesController {
    private final PostLikesService postLikesService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likes(@PathVariable @Min(1) Long id){
        Long userId = jwtTokenProvider.getUserIdFromSecurity();

        postLikesService.likes(new PostLikesDto(userId, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
