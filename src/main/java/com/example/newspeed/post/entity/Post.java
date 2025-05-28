package com.example.newspeed.post.entity;

import com.example.newspeed.post.dto.PostTitleOfUserDto;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
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

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Post() {
    }

    public Post(String title, String contents, String imageUrl) {
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
    }

    // Post 객체를 PostTitleOfUserDto 로 변환
    // Post > title 을 전달하는 용도로 가공하는 메서드
    public PostTitleOfUserDto toDto() {
        return new PostTitleOfUserDto(this.title);
    }

}
