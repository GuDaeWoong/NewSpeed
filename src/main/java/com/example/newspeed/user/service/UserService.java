package com.example.newspeed.user.service;


import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.user.dto.CreateUserResponseDto;
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

    // 유저 생성 (회원가입)
    @Transactional
    public CreateUserResponseDto createUser(String email, String nickname, String userUrl, String password) {
        // 암호화
        String encodePassword = passwordManager.encodePassword(password);

        // 유저 생성
        User user = new User(email, nickname, userUrl, encodePassword);
        return CreateUserResponseDto.toDto(userRepository.save(user));
    }

    @Transactional
    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.get();
    }
}
