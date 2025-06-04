package com.example.newspeed.comment.entity;

import com.example.newspeed.comment.dto.CommentRequestDto;
import com.example.newspeed.global.entity.BaseEntity;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull
    private String contents;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<CommentLikes> commentLikes = new ArrayList<>();

    public Comment(User user, Post post, String contents) {
        this.user = user;
        this.post = post;
        this.contents = contents;
    }

    public Comment() {

    }

    public void updateComment(@Valid CommentRequestDto commentRequestDto) {
        this.contents = commentRequestDto.getContents();
    }
}
