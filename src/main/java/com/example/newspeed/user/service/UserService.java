package com.example.newspeed.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.global.dto.PageResponseDto;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.dto.UserCreateResponseDto;
import com.example.newspeed.user.dto.UserFindResponseDto;
import com.example.newspeed.user.dto.UserFindWithFollowResponseDto;
import com.example.newspeed.user.dto.UserUpdateProfileResponseDto;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordManager passwordManager;
    private final FollowService followService;

    // 유저 생성 (회원가입)
    @Transactional
    public UserCreateResponseDto createUser(String email, String nickname, String userUrl, String password) {
        // 중복 이메일이라면 예외처리
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 암호화
        String encodePassword = passwordManager.encodePassword(password);

        // 유저 생성
        User user = new User(email, nickname, userUrl, encodePassword);
        return UserCreateResponseDto.toDto(userRepository.save(user));
    }


    /**
     * 특정 유저 조회
     * @param userId 조회할 유저 ID
     * @return 조회 유저 정보 + 조회 유저의 팔로우 수, 팔로워 수, 작성 게시글 수 반환
     */

    public UserFindResponseDto findByIdUser(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 팔로우 수 카운팅
        long followCount = followService.getFollowCount(userId);

        // 팔로워(팔로우 받은) 수 카운팅
        long followedCount = followService.getFollowedCount(userId);

        // 유저가 작성한 게시글 수 카운팅
        Long postCount = userRepository.countPostsByUserId(userId);

        return UserFindResponseDto.toDto(findUser, followCount, followedCount, postCount);
    }


    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 유저 조회
    public PageResponseDto<UserFindResponseDto> findAllUsersPaged(Pageable pageable) {

        // Pageable의 페이지 번호가 1부터 시작한다고 가정하고, 0부터 시작하는 Pageable로 변환
        int pageNumber = pageable.getPageNumber()-1 ;
        // 요청 페이지 번호가 음수가 되는 것을 방지
        if (pageNumber < 0) {
            pageNumber = 0;
        }

        // 페이지 조회
        Pageable adjustedPageable = PageRequest.of(pageNumber, pageable.getPageSize(), pageable.getSort());
        Page<User> userPage = userRepository.findAll(adjustedPageable);

        // 페이지 수를 넘어가는 요청 시 예외 처리
        if (userPage.isEmpty() && pageNumber >= userPage.getTotalPages()) {
            throw new CustomException(ErrorCode.PAGE_NOT_FOUND);
        }

        // 전체 유저 당 팔로우/팔로워/게시글 수 미리 조회
        Map<Long, Long> followCounts = followService.getFollowCountMap();
        Map<Long, Long> followedCounts = followService.getFollowedCountMap();
        Map<Long, Long> postCounts = getFollowedCountMap();

        // 응담 Dto에 맞춰 변환
        List<UserFindResponseDto> responseDtos = new ArrayList<>();
        for (User user : userPage) {
            long followCount = followCounts.getOrDefault(user.getId(), 0L);
            long followedCount = followedCounts.getOrDefault(user.getId(), 0L);
            long postCount = postCounts.getOrDefault(user.getId(), 0L);

            responseDtos.add(UserFindResponseDto.toDto(user, followCount, followedCount, postCount));
        }

        // 페이지Dto에 적용하여 반환
        Page<UserFindResponseDto> dtoPage = new PageImpl<>(responseDtos, adjustedPageable, userPage.getTotalElements());
        return new PageResponseDto<>(dtoPage);
    }

    // 유저 전체 조회 + 팔로우 여부 체크
    public PageResponseDto<UserFindWithFollowResponseDto> findUserWithFollow(Long userId, Pageable pageable) {

        // Pageable의 페이지 번호가 1부터 시작한다고 가정하고, 0부터 시작하는 Pageable로 변환
        int pageNumber = pageable.getPageNumber()-1 ;
        // 요청 페이지 번호가 음수가 되는 것을 방지
        if (pageNumber < 0) {
            pageNumber = 0;
        }

        // 페이지 조회
        Pageable adjustedPageable = PageRequest.of(pageNumber, pageable.getPageSize(), pageable.getSort());
        Page<User> userPage = userRepository.findAllByIdNot(userId, adjustedPageable);

        // 페이지 수를 넘어가는 요청 시 예외 처리
        if (userPage.isEmpty() && pageNumber >= userPage.getTotalPages()) {
            throw new CustomException(ErrorCode.PAGE_NOT_FOUND);
        }

        // 로그인한 유저가 팔로우 중인 유저들의 ID를 조회
        List<Long> followingIdList = followService.findFollowIdsByUserId(userId);

        // 응담 Dto에 맞춰 변환
        List<UserFindWithFollowResponseDto> responseDtos = new ArrayList<>();
        for (User user : userPage) {
            boolean isFollow = followingIdList.contains(user.getId());
            responseDtos.add(UserFindWithFollowResponseDto.toDto(user, isFollow));
        }

        // 페이지Dto에 적용하여 반환
        Page<UserFindWithFollowResponseDto> dtoPage = new PageImpl<>(responseDtos, adjustedPageable, userPage.getTotalElements());
        return new PageResponseDto<>(dtoPage);
    }

    // 유저 프로필 수정
    @Transactional
    public UserUpdateProfileResponseDto updateProfile(Long userId, String nickname, String userUrl, String password) {

        User findUser = userRepository.findByIdOrElseThrow(userId);

        // Dto의 password와 현재 DB의 비밀번호가 일치하는지 확인
        passwordManager.validatePasswordMatchOrThrow(password, findUser.getPassword());

        // 프로필 수정
        findUser.updateProfile(nickname, userUrl);
        userRepository.save(findUser);

        return UserUpdateProfileResponseDto.toDto(findUser);
    }

    // 유저 비밀번호 수정
    @Transactional
    public void updatePassword(Long userId, String currentPassword, String newPassword) {

        User findUser = userRepository.findByIdOrElseThrow(userId);

        // 요청 받은 currentPassword와 현재 DB의 비밀번호가 일치하는지 확인
        passwordManager.validatePasswordMatchOrThrow(currentPassword, findUser.getPassword());

        // 암호화
        String encodePassword = passwordManager.encodePassword(newPassword);

        // 비밀번호 수정
        findUser.updatePassword(encodePassword);
        userRepository.save(findUser);
    }

    // 유저 삭제
    @Transactional
    public void deleteUser(Long userId, String password) {

        User findUser = userRepository.findByIdOrElseThrow(userId);

        // 요청 받은 currentPassword와 현재 DB의 비밀번호가 일치하는지 확인
        passwordManager.validatePasswordMatchOrThrow(password, findUser.getPassword());

        userRepository.deleteById(userId);
    }

    public Map<Long, Long> getFollowedCountMap() {
        List<Object[]> countPostList = userRepository.countPostsGroupedByUser();

        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : countPostList) {
            Long userId = (Long) row[0];
            Long count = (Long) row[1];
            map.put(userId, count);
        }
        return map;
    }
}
