package com.example.newspeed.user.service;


import com.example.newspeed.global.common.PasswordManager;
import com.example.newspeed.user.dto.CreateUserResponseDto;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordManager passwordManager;

    @Transactional
    public CreateUserResponseDto createUser(String email, String nickname, String userUrl, String password) {
        // 암호화
        String encodePassword = passwordManager.encodePassword(password);

        // 유저 생성
        User user = new User(email, nickname, userUrl, encodePassword);
        return CreateUserResponseDto.toDto(userRepository.save(user));
    }

    public User findUserById(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        return user;
    }
}
