package com.example.newspeed.user.repository;

import com.example.newspeed.user.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByUserIdAndFollowId(Long userId, Long followId);

    // 유저가 팔로우한 유저 수 카운트
    long countByUserId(Long userId);

    // 유저가 팔로우를 받은 유저 수 카운트
    long countByFollowId(Long followId);

    boolean existsByUserIdAndFollowId(Long userId, Long followId);

    @Query("select f.follow.id from Follow f where f.user.id = :userId")
    List<Long> findFollowIdsByUserId(@Param("userId") Long userId);

    // 유저, 유저당 팔로우한 유저 카운트
    @Query("SELECT f.user.id, COUNT(f) FROM Follow f GROUP BY f.user.id")
    List<Object[]> countAllGroupedByUserId();

    // 유저, 유저당 팔로우 받은 유저 카운트
    @Query("SELECT f.follow.id, COUNT(f) FROM Follow f GROUP BY f.follow.id")
    List<Object[]> countAllGroupedByFollowId();
}
