package com.example.newspeed.user.dto;

import com.example.newspeed.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserFindResponseDto {

    private final Long userId;

    private final String email;

    private final String nickname;

    private final String userUrl;

    private final Long followCount; // 팔로우 수

    private final Long followerCount; // 팔로워 수

    private final Long postCount; // 게시글 수

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    public UserFindResponseDto(
            Long userId, String email, String nickname, String userUrl,
            Long followCount, Long followerCount, Long postCount,
            LocalDateTime createdAt, LocalDateTime modifiedAt)
    {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.userUrl = userUrl;
        this.followCount = followCount;
        this.followerCount = followerCount;
        this.postCount = postCount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static UserFindResponseDto toDto (User user, long followCount, long followerCount, Long postCount) {
        return new UserFindResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserUrl(),
                followCount, // 팔로우 수: User 가 팔로우한 유저 수
                followerCount, // 팔로워 수: User 가 팔로워 받은 유저 수
                postCount, // 게시글 수: User 가 작성한 게시글 수
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }
}
