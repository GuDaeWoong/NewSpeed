package com.example.newspeed.user.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newspeed.auth.dto.CustomUserDetails;
import com.example.newspeed.auth.dto.TokenDto;
import com.example.newspeed.auth.jwt.TokenCookieUtils;
import com.example.newspeed.auth.jwt.TokenExtractor;
import com.example.newspeed.auth.service.AuthService;
import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.dto.PageResponseDto;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.dto.*;
import com.example.newspeed.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final TokenExtractor tokenExtractor;
    private final TokenCookieUtils tokenCookieUtils;

    /**
     * 유저 생성 (회원가입)
     *
     * @param requestDto 이메일, 닉네임, 프로필이미지url, 패스워드
     * @return 생성된 유저 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<UserCreateResponseDto> createUser(@Valid @RequestBody UserCreateRequestDto requestDto) {

        UserCreateResponseDto responseDto = userService.createUser(requestDto.getEmail(),
                                                                   requestDto.getNickname(),
                                                                   requestDto.getUserUrl(),
                                                                   requestDto.getPassword());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserFindResponseDto> findByIdUser(@PathVariable Long userId) {

        UserFindResponseDto userFindResponseDto = userService.findByIdUser(userId);

        return new ResponseEntity<>(userFindResponseDto, HttpStatus.OK);
    }

    /**
     * 로그인 유저 정보 조회 (마이페이지 조회)
     * @param userDetails 로그인 유저 정보를 담고 있는 객체
     * @return 정상 조회 시 로그인된 유저의 유저 정보 + 200 OK 반환
     */
    @GetMapping("/me")
    public ResponseEntity<UserFindResponseDto> findMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            throw new CustomException(ErrorCode.REQUIRED_LOGIN);
        }

        Long currentUserId = userDetails.getId();

        UserFindResponseDto userFindResponseDto = userService.findByIdUser(currentUserId);

        return new ResponseEntity<>(userFindResponseDto, HttpStatus.OK);
    }

    /**
     * 전체 유저 조회
     *
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<UserFindResponseDto>> findAllUsers(@PageableDefault(page = 1) Pageable pageable) {

        PageResponseDto<UserFindResponseDto> findUserResponseDto = userService.findAllUsersPaged(pageable);

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
    public ResponseEntity<PageResponseDto<UserFindWithFollowResponseDto>> findUsersWithFollow(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PageableDefault(page = 1) Pageable pageable) {

        Long currentUserId = userDetails.getId();

        PageResponseDto<UserFindWithFollowResponseDto> userWithFollow = userService.findUserWithFollow(currentUserId, pageable);

        return new ResponseEntity<>(userWithFollow, HttpStatus.OK);
    }

    /**
     * 로그인 유저 프로필 수정
     *
     * @param requestDto 닉네임, 프로필이미지url, 패스워드
     * @return 로그인 유저 id + 변경 후 닉네임, 프로필이미지url
     */
    @PatchMapping("/profile")
    public ResponseEntity<UserUpdateProfileResponseDto> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserUpdateProfileRequestDto requestDto) {

        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = userDetails.getId();

        UserUpdateProfileResponseDto responseDto = userService.updateProfile(currentUserId,
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
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,@Valid @RequestBody UserUpdatePasswordRequestDto requestDto) {

        Long currentUserId = userDetails.getId();

        userService.updatePassword(currentUserId,
                requestDto.getCurrentPassword(),
                requestDto.getNewPassword());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 유저 삭제 (회원탈퇴)
     *
     * @param requestDto 비밀번호
     * @param request
     * @param response
     * @return -
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails,@Valid @RequestBody UserDeleteRequestDto requestDto,
                                           HttpServletRequest request, HttpServletResponse response) {

        Long currentUserId = userDetails.getId();

        //Header 에서 Token 가져오기
        String accessToken = tokenExtractor.extractAccessTokenFromHeader(request).orElse(null);
        String refreshToken = tokenExtractor.extractRefreshTokenFromCookie(request).orElse(null);

        authService.logout(new TokenDto(accessToken,refreshToken));

        //쿠키에서 refreshToken 리셋
        tokenCookieUtils.deleteRefreshTokenCookie(response);

        userService.deleteUser(currentUserId, requestDto.getPassword());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}