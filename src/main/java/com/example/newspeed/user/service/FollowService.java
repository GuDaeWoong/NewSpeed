package com.example.newspeed.user.service;

import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.dto.FollowRequestDto;
import com.example.newspeed.user.entity.Follow;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.FollowRepository;
import com.example.newspeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void follow(FollowRequestDto followRequestDto) {
        User user = getUserById(followRequestDto.getUserId());
        User follow = getUserById(followRequestDto.getFollowId());

        if (followRepository.findByUserIdAndFollowId(user.getId(), follow.getId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_FOLLOWING);
        }

        followRepository.save(new Follow(user, follow));
    }

    @Transactional
    public void unFollow(FollowRequestDto followRequestDto) {
        User user = getUserById(followRequestDto.getUserId());
        User follow = getUserById(followRequestDto.getFollowId());

        Follow findFollow = followRepository.findByUserIdAndFollowId(user.getId(), follow.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOLLOWING));

        followRepository.delete(findFollow);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
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
