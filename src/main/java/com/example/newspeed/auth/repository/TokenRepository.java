package com.example.newspeed.auth.repository;

import com.example.newspeed.user.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String refreshToken);
}
