package com.example.newspeed.user.controller;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.user.dto.followDto;
import com.example.newspeed.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/follow")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/{id}")
    public ResponseEntity<Void> follow(@PathVariable Long id){

        Long userId = jwtTokenProvider.getUserIdFromSecurity();

        followService.follow(new followDto(userId, id));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
