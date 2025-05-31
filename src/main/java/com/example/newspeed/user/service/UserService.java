package com.example.newspeed.user.service;


import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.dto.CreateUserResponseDto;
import com.example.newspeed.user.dto.FindUserResponseDto;
import com.example.newspeed.user.dto.FindUserWithFollowResponseDto;
import com.example.newspeed.user.dto.UpdateProfileResponseDto;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordManager passwordManager;
    private final FollowService followService;
//    private final PostService postService;

    // 유저 생성 (회원가입)
    @Transactional
    public CreateUserResponseDto createUser(String email, String nickname, String userUrl, String password) {
        // 암호화
        String encodePassword = passwordManager.encodePassword(password);

        // 유저 생성
        User user = new User(email, nickname, userUrl, encodePassword);
        return CreateUserResponseDto.toDto(userRepository.save(user));
    }


    public FindUserResponseDto findByIdUser(Long userId) {
        User findUser = userRepository.findByIdOrElseThrow(userId);

        // 팔로우 수 카운팅
        long followCount = followService.getFollowCount(userId);

        // 팔로워(팔로우 받은) 수 카운팅
        long followedCount = followService.getFollowedCount(userId);

        // 유저가 작성한 게시글 수 카운팅
        Long postCount = userRepository.countPostsByUserId(userId);

        return FindUserResponseDto.toDto(findUser, followCount, followedCount, postCount);
    }


    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.get();
    }

    // 유저 조회
    public List<FindUserResponseDto> findAllUsersPaged(Pageable pageable) {

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
        List<FindUserResponseDto> responseDtos = new ArrayList<>();
        for (User user : userPage) {
            long followCount = followCounts.getOrDefault(user.getId(), 0L);
            long followedCount = followedCounts.getOrDefault(user.getId(), 0L);
            long postCount = postCounts.getOrDefault(user.getId(), 0L);

            responseDtos.add(FindUserResponseDto.toDto(user, followCount, followedCount, postCount));
        }

        return responseDtos;
    }

    // 유저 전체 조회 + 팔로우 여부 체크
    public List<FindUserWithFollowResponseDto> findUserWithFollow(Long userId, Pageable pageable) {

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
        List<FindUserWithFollowResponseDto> responseDtos = new ArrayList<>();
        for (User user : userPage) {
            boolean isFollow = followingIdList.contains(user.getId());
            responseDtos.add(FindUserWithFollowResponseDto.toDto(user, isFollow));
        }

        return responseDtos;
    }

    // 유저 프로필 수정
    @Transactional
    public UpdateProfileResponseDto updateProfile(Long userId, String nickname, String userUrl, String password) {

        User findUser = userRepository.findByIdOrElseThrow(userId);

        // Dto의 password와 현재 DB의 비밀번호가 일치하는지 확인
        passwordManager.validatePasswordMatchOrThrow(password, findUser.getPassword());

        // 프로필 수정
        findUser.updateProfile(nickname, userUrl);
        userRepository.save(findUser);

        return UpdateProfileResponseDto.toDto(findUser);
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
