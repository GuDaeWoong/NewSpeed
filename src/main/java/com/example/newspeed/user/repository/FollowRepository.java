package com.example.newspeed.user.repository;

import com.example.newspeed.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByUserIdAndFollowId(Long userId, Long followId);

    // 유저가 팔로우한 유저 수 카운트
    long countByUserId(Long userId);

    // 유저가 팔로우를 받은 유저 수 카운트
    long countByFollowId(Long followId);

}
