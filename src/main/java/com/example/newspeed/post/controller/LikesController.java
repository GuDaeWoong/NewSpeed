package com.example.newspeed.post.controller;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.post.dto.LikesDto;
import com.example.newspeed.post.service.LikesService;
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
@RequestMapping("/api/posts/{id}/likes")
@RequiredArgsConstructor
@Validated
public class LikesController {
    private final LikesService likesService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> likes(@PathVariable @Min(1) Long id){
        Long userId = jwtTokenProvider.getUserIdFromSecurity();

        likesService.likes(new LikesDto(userId, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
