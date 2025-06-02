package com.example.newspeed.post.service;

import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.post.dto.PostLikesDto;
import com.example.newspeed.post.entity.PostLikes;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.repository.PostLikesRepository;
import com.example.newspeed.post.repository.PostRepository;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikesService {

    private final UserService userService;
    private final PostRepository postRepository;
    private final PostLikesRepository postLikesRepository;

    public void addLikes(PostLikesDto postLikesDto) {

        User user = getUserById(postLikesDto.getUserId());

        Post post = getPostById(postLikesDto.getPostId());

        if(postLikesRepository.findByUserIdAndPostId(user.getId(), post.getId()).isPresent()){
            throw new CustomException(ErrorCode.ALREADY_LIKE);
        };

        postLikesRepository.save(new PostLikes(user, post));
    }

    public void deleteLikes(PostLikesDto postLikesDto) {

        User user = getUserById(postLikesDto.getUserId());

        Post post = getPostById(postLikesDto.getPostId());

        PostLikes findPostLikes = postLikesRepository.findByUserIdAndPostId(user.getId(), post.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_LIKE));

        postLikesRepository.delete(findPostLikes);
    }

    private User getUserById(Long id) {
        return userService.findUserById(id);
    }

    private Post getPostById(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
