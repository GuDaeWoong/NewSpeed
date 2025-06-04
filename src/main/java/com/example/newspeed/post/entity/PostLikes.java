package com.example.newspeed.post.entity;

import com.example.newspeed.global.entity.BaseEntity;
import com.example.newspeed.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name="postLikes")
public class PostLikes extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public PostLikes() {
    }

    public PostLikes(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}