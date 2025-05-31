package com.example.newspeed.user.controller;

import com.example.newspeed.auth.jwt.JwtTokenProvider;
import com.example.newspeed.auth.service.AuthService;
import com.example.newspeed.user.dto.*;
import com.example.newspeed.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 유저 생성 (회원가입)
     *
     * @param requestDto 이메일, 닉네임, 프로필이미지url, 패스워드
     * @return 생성된 유저 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<CreateUserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {

        CreateUserResponseDto responseDto = userService.createUser(requestDto.getEmail(),
                                                                   requestDto.getNickname(),
                                                                   requestDto.getUserUrl(),
                                                                   requestDto.getPassword());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<FindUserResponseDto> findByIdUser(@PathVariable Long userId) {

        FindUserResponseDto findUserResponseDto = userService.findByIdUser(userId);

        return new ResponseEntity<>(findUserResponseDto, HttpStatus.OK);
    }

    /**
     * 로그인 유저 정보 조회 (마이페이지 조회)
     * @return 정상 조회 시 JWT 토큰으로 확인한 로그인 유저의 유저 정보 + 200 OK 반환
     */
    @GetMapping("/me")
    public ResponseEntity<FindUserResponseDto> findMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails.getId();

        FindUserResponseDto findUserResponseDto = userService.findByIdUser(currentUserId);

        return new ResponseEntity<>(findUserResponseDto, HttpStatus.OK);
    }

    /**
     * 전체 유저 조회
     *
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<List<FindUserResponseDto>> findAllUsers(@PageableDefault(page = 1) Pageable pageable) {

        List<FindUserResponseDto> findUserResponseDto = userService.findAllUsersPaged(pageable);

        return new ResponseEntity<>(findUserResponseDto, HttpStatus.OK);
    }

    /**
     * 유저 전체 조회 + 팔로우 여부 체크
     *
     * @param userDetails
     * @param pageable
     * @return
     */
    @GetMapping("/with-follow")
    public ResponseEntity<List<FindUserWithFollowResponseDto>> findUsersWithFollow(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PageableDefault(page = 1) Pageable pageable) {

        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = userDetails.getId();

        List<FindUserWithFollowResponseDto> userWithFollow = userService.findUserWithFollow(currentUserId, pageable);

        return new ResponseEntity<>(userWithFollow, HttpStatus.OK);
    }

    /**
     * 로그인 유저 프로필 수정
     *
     * @param requestDto 닉네임, 프로필이미지url, 패스워드
     * @return 로그인 유저 id + 변경 후 닉네임, 프로필이미지url
     */
    @PatchMapping("/profile")
    public ResponseEntity<UpdateProfileResponseDto> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UpdateProfileRequestDto requestDto) {

        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = userDetails.getId();

        UpdateProfileResponseDto responseDto = userService.updateProfile(currentUserId,
                requestDto.getNickname(),
                requestDto.getUserUrl(),
                requestDto.getPassword());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 로그인 유저 비밀번호 수정
     *
     * @param requestDto 닉네임, 프로필이미지url, 패스워드
     * @return -
     */
    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,@Valid @RequestBody UpdatePasswordRequestDto requestDto) {

        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = userDetails.getId();

        userService.updatePassword(currentUserId,
                requestDto.getCurrentPassword(),
                requestDto.getNewPassword());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

        /**
         * 유저 생성 (회원탈퇴)
         *
         * @param requestDto 비밀번호
         * @param request
         * @param response
         * @return -
         */

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails,@Valid @RequestBody DeleteUserRequestDto requestDto,
                                           HttpServletRequest request, HttpServletResponse response) {

        Long currentUserId = userDetails.getId();
      
        authService.logout(request, response);

        userService.deleteUser(currentUserId, requestDto.getPassword());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}