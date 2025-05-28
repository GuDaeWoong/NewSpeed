package com.example.newspeed.user.repository;

import com.example.newspeed.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
}
