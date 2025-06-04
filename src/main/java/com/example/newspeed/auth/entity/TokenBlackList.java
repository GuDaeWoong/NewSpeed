package com.example.newspeed.auth.entity;

import com.example.newspeed.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "tokenBlackList")
public class TokenBlackList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accessToken;

    public TokenBlackList() {
    }

    public TokenBlackList(String accessToken) {
        this.accessToken = accessToken;
    }
}
