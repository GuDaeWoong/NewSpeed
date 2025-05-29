package com.example.newspeed.post.service;

import com.example.newspeed.post.dto.LikesDto;
import com.example.newspeed.post.entity.Likes;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.LikesRepository;
import com.example.newspeed.post.repository.PostRepository;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;

    public void likes(LikesDto likesDto) {
        Long userId = likesDto.getUserId();
        Long postId = likesDto.getPostId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("요청자 유저 없음"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요 대상 게시글 없음"));

        Optional<Likes> checkedLikes = likesRepository.findByUserIdAndPostId(userId, postId);

        if(checkedLikes.isPresent()){
            // 좋아요 상태면 안좋아요
            likesRepository.delete(checkedLikes.get());
        }else{
            // 안좋아요 상태면 좋아요
            likesRepository.save(new Likes(user, post));
        }

    }
}
