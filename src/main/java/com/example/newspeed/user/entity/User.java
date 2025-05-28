package com.example.newspeed.user.entity;

import com.example.newspeed.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String userUrl;

    @Column(nullable = false)
    private String password;

    public User() {
    }

    public User(String email, String username, String password) {
        this.email = email;
        this.nickname = username;
        this.password = password;
    }
}
