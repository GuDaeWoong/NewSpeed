package com.example.newspeed.comment.entity;

import com.example.newspeed.global.entity.BaseEntity;
import com.example.newspeed.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name="commentLikes")
public class CommentLikes extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public CommentLikes() {
    }

    public CommentLikes(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }
}
