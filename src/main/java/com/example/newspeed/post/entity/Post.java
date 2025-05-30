package com.example.newspeed.post.entity;

import com.example.newspeed.comment.entity.Comment;
import com.example.newspeed.global.entity.BaseEntity;
import com.example.newspeed.post.dto.PostTitleOfUserDto;
import com.example.newspeed.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "post")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String userUrl;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostLikes> postLikes = new ArrayList<>();

    public Post() {
    }

    public Post(User user, String title, String contents, String imageUrl) {
        this.user = user;
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
        this.userUrl = user.getUserUrl();
    }

    // Post 객체를 PostTitleOfUserDto 로 변환
    // Post > title 을 전달하는 용도로 가공하는 메서드
    public PostTitleOfUserDto toDto() {
        return new PostTitleOfUserDto(this.title);
    }

}
