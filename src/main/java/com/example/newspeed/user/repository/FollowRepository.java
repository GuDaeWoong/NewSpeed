package com.example.newspeed.user.repository;

import com.example.newspeed.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByUserIdAndFollowId(Long userId, Long followId);

}
