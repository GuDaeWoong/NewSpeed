package com.example.newspeed.user.service;

import com.example.newspeed.user.dto.followDto;
import com.example.newspeed.user.entity.Follow;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.FollowRepository;
import com.example.newspeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public void follow(followDto followDto) {
        Long userId = followDto.getUserId();
        Long followId = followDto.getFollowId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("요청자 유저 없음"));

        User follow = userRepository.findById(followId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 대상 유저 없음"));

        Optional<Follow> checkedFollow = followRepository.findByUserIdAndFollowId(userId, followId);

        if (checkedFollow.isPresent()) {
            // 팔로우 상태면 언팔로우
            followRepository.delete(checkedFollow.get());
        } else {
            // 팔로우 안되어 있으면 팔로우 추가
            followRepository.save(new Follow(user, follow));
        }
    }

    // 팔로우 수 카운팅 메서드
    public long getFollowCount(Long userId) {
        return followRepository.countByUserId(userId);
    }


}
