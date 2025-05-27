use newspeed;

CREATE TABLE user
(
    user_id     BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '유저 식별자',
    nickname    VARCHAR(20)  NOT NULL COMMENT '유저 이름',
    email       VARCHAR(250) NOT NULL UNIQUE COMMENT '이메일',
    password    VARCHAR(25)  NOT NULL COMMENT '비밀번호',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간'
);

CREATE TABLE post
(
    post_id     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 식별자',
    title       VARCHAR(255) COMMENT '게시글 제목',
    content     LONGTEXT COMMENT '게시글 내용',
    image_url   LONGTEXT COMMENT '게시글 사진',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    user_id BIGINT COMMENT '유저 식별자',
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);