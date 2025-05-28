package com.example.newspeed.user.entity;

import com.example.newspeed.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "follow")
public class Follow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "follow_id")
    private User follow;

    public Follow() {
    }

    public Follow(User user, User follow) {
        this.user = user;
        this.follow = follow;
    }
}
