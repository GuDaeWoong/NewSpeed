package com.example.newspeed.user.repository;

import com.example.newspeed.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 요청받은 id에 해당하는 유저 조회 > 해당 유저데이터가 없을 경우 예외 처리
     * @param userId 조회할 유저 ID
     * @return ID 값으로 조회한 유저 데이터
     * @throws ResponseStatusException 유저 데이터가 없을 경우 > 404 상태 코드로 예외 처리
     */
    default User findByIdOrElseThrow(Long userId) {
        return findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + userId));
    }


    Page<User> findAllByIdNot(Long userId, Pageable pageable);

    // 유저가 작성한 게시글 수 카운팅
    @Query("SELECT count(p) " +
            "FROM User u " +
            "JOIN Post p ON u.id = p.user.id " +
            "WHERE u.id = ?1")
    Long countPostsByUserId(Long userId);

    // 유저, 유저당 포스트 수 카운트
    @Query("SELECT u.id, COUNT(p) FROM User u LEFT JOIN u.posts p GROUP BY u.id")
    List<Object[]> countPostsGroupedByUser();
}
