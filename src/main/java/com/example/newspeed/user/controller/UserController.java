package com.example.newspeed.user.controller;

import com.example.newspeed.global.common.JwtTokenProvider;
import com.example.newspeed.user.dto.*;
import com.example.newspeed.user.service.AuthService;
import com.example.newspeed.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 로그인 유저 프로필 수정
     *
     * @param requestDto 닉네임, 프로필이미지url, 패스워드
     * @return 로그인 유저 id + 변경 후 닉네임, 프로필이미지url
     */
    @PatchMapping("/profile")
    public ResponseEntity<UpdateProfileResponseDto> updateProfile(@RequestBody UpdateProfileRequestDto requestDto) {

        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();

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
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto requestDto) {

        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();

        userService.updatePassword(currentUserId,
                                   requestDto.getCurrentPassword(),
                                   requestDto.getNewPassword());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 로그인 유저 정보 조회 (마이페이지 조회)
     * @return 정상 조회 시 JWT 토큰으로 확인한 로그인 유저의 유저 정보 + 200 OK 반환
     */
    @GetMapping("/me")
    public ResponseEntity<FindUserResponseDto> findMyPage() {
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();

        FindUserResponseDto findUserResponseDto = userService.findByIdUser(currentUserId);

        return new ResponseEntity<>(findUserResponseDto, HttpStatus.OK);
    }

    /**
     * 전체 유저 조회
     *
     * @param page 페이지
     * @param size 페이지 사이즈
     * @return 조회된 유저 리스트
     */
    @GetMapping
    public ResponseEntity<List<FindUserResponseDto>> findAllUsers(@RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {

        List<FindUserResponseDto> findUserResponseDto = userService.findAllUsersPaged(page, size);

        return new ResponseEntity<>(findUserResponseDto, HttpStatus.OK);
    }


    @GetMapping("/with-follow")
    public ResponseEntity<List<FindUserWithFollowResponseDto>> findUsersWithFollow(@RequestParam(defaultValue = "1") int page,
                                                                                   @RequestParam(defaultValue = "10") int size) {

        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();

        List<FindUserWithFollowResponseDto> userWithFollow = userService.findUserWithFollow(currentUserId, page, size);

        return new ResponseEntity<>(userWithFollow, HttpStatus.OK);
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
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody DeleteUserRequestDto requestDto,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {

        // JwtTokenProvider를 통해 로그인 유저 ID 가져오기
        Long currentUserId = jwtTokenProvider.getUserIdFromSecurity();

        userService.deleteUser(currentUserId, requestDto.getPassword());

        authService.logout(request, response);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}