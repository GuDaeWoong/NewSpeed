package com.example.newspeed.auth.repository;

import com.example.newspeed.user.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {
    boolean existsByAccessToken(String accessToken);
}
