package com.example.newspeed.user.service;


import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.user.dto.CreateUserResponseDto;
import com.example.newspeed.user.dto.FindUserResponseDto;
import com.example.newspeed.user.dto.UpdateProfileResponseDto;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordManager passwordManager;
    private final FollowService followService;

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

        long followCount = followService.getFollowCount(userId);

        return FindUserResponseDto.toDto(findUser, followCount);
    }


    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.get();
    }

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
}
