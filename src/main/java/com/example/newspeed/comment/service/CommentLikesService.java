package com.example.newspeed.comment.service;

import com.example.newspeed.comment.dto.CommentLikesDto;
import com.example.newspeed.comment.entity.Comment;
import com.example.newspeed.comment.entity.CommentLikes;
import com.example.newspeed.comment.repository.CommentLikesRepository;
import com.example.newspeed.comment.repository.CommentRepository;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikesService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;

    public void likes(CommentLikesDto commentLikesDto) {
        Long userId = commentLikesDto.getUserId();
        Long commentId = commentLikesDto.getCommentId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("요청자 유저 없음"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요 대상 게시글 없음"));

        Optional<CommentLikes> checkedLikes = commentLikesRepository.findByUserIdAndCommentId(userId, commentId);

        if(checkedLikes.isPresent()){
            // 좋아요 상태면 안좋아요
            commentLikesRepository.delete(checkedLikes.get());
        }else{
            // 안좋아요 상태면 좋아요
            commentLikesRepository.save(new CommentLikes(user, comment));
        }
    }
}
