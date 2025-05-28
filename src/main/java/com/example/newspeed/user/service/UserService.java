package com.example.newspeed.user.service;


import com.example.newspeed.user.dto.CreateUserResponseDto;
import com.example.newspeed.user.dto.FindUserResponseDto;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public CreateUserResponseDto createUser(String email, String nickname, String password) {

        User user = new User(email, nickname, password);

        return CreateUserResponseDto.toDto(userRepository.save(user));
    }


    public FindUserResponseDto findByIdUser(Long userId) {
        User findUser = userRepository.findByIdOrElseThrow(userId);

        return FindUserResponseDto.toDto(findUser);
    }

}
