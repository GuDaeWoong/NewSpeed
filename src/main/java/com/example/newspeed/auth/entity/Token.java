package com.example.newspeed.auth.entity;

import com.example.newspeed.global.entity.BaseEntity;
import com.example.newspeed.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name="token")
public class Token extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String refreshToken;


    public Token() {

    }

    public Token(User user, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
    }

}
