package com.example.newspeed.user.service;

import com.example.newspeed.user.dto.followDto;
import com.example.newspeed.user.entity.Follow;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.FollowRepository;
import com.example.newspeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // 팔로워 수 카운팅 메서드
    public long getFollowedCount(Long userId) {
        return followRepository.countByFollowId(userId);
    }

    // 유저가 팔로우 하고 있는 대상인지 확인하는 메서드
    public boolean isFollow(Long userId, Long followId) {
        return followRepository.existsByUserIdAndFollowId(userId, followId);
    }

    // 팔로우하고 있는 유저id 리스트를 반환하는 메서드
    public List<Long> findFollowIdsByUserId(Long userId) {
        return followRepository.findFollowIdsByUserId(userId);
    }

    public Map<Long, Long> getFollowCountMap() {
        List<Object[]> countFollowList = followRepository.countAllGroupedByUserId();

        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : countFollowList) {
            Long userId = (Long) row[0];
            Long count = (Long) row[1];
            map.put(userId, count);
        }
        return map;
    }

    public Map<Long, Long> getFollowedCountMap() {
        List<Object[]> countFollowedList = followRepository.countAllGroupedByFollowId();

        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : countFollowedList) {
            Long userId = (Long) row[0];
            Long count = (Long) row[1];
            map.put(userId, count);
        }
        return map;
    }
}
