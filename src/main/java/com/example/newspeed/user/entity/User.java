package com.example.newspeed.user.entity;

import com.example.newspeed.auth.entity.Token;
import com.example.newspeed.global.entity.BaseEntity;
import com.example.newspeed.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Token> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Follow> following = new ArrayList<>();

    @OneToMany(mappedBy = "follow", cascade = CascadeType.REMOVE)
    private List<Follow> followers = new ArrayList<>();

    // 유저와 관련된 post 들을 한 번에 조회를 위함 -> User -> Post 1:N 관계 설정
    @OneToMany(mappedBy = "user",  cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();

    public User() {
    }

    public User(String email, String nickname, String userUrl, String password) {
        this.email = email;
        this.nickname = nickname;
        this.userUrl = userUrl;
        this.password = password;
    }

    public void updateProfile(String nickname, String userUrl) {
        if (Strings.isNotBlank(nickname)) {
            this.nickname = nickname;
        }

        if (Strings.isNotBlank(userUrl)) {
            this.userUrl = userUrl;
        }
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
