package com.example.newspeed.user.controller;

import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.dto.CustomUserDetails;
import com.example.newspeed.user.dto.followDto;
import com.example.newspeed.user.service.FollowService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow/{id}")
    public ResponseEntity<Void> follow(@PathVariable @Min(1) Long id, @AuthenticationPrincipal CustomUserDetails userDetails){

        Long userId = userDetails.getId();

        //같은 유저는 팔로우 할 수 없음 -> 예외 처리
        if(Objects.equals(userId, id)) throw new CustomException(ErrorCode.CANNOT_FOLLOW_SELF);

        followService.follow(new followDto(userId, id));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
