package com.example.newspeed.comment.service;

import com.example.newspeed.comment.dto.CommentLikesDto;
import com.example.newspeed.comment.entity.Comment;
import com.example.newspeed.comment.entity.CommentLikes;
import com.example.newspeed.comment.repository.CommentLikesRepository;
import com.example.newspeed.comment.repository.CommentRepository;
import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.repository.UserRepository;
import com.example.newspeed.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikesService {
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;

    public void addLikes(CommentLikesDto commentLikesDto) {

        User user = getUserById(commentLikesDto.getUserId());

        Comment comment = getCommentById(commentLikesDto.getCommentId());

        if(commentLikesRepository.findByUserIdAndCommentId(user.getId(), comment.getId()).isPresent()){
            throw new CustomException(ErrorCode.ALREADY_LIKE);
        };

        commentLikesRepository.save(new CommentLikes(user, comment));

    }

    public void deleteLikes(CommentLikesDto commentLikesDto) {
        User user = getUserById(commentLikesDto.getUserId());

        Comment comment = getCommentById(commentLikesDto.getCommentId());

        CommentLikes findCommentLikes = commentLikesRepository.findByUserIdAndCommentId(user.getId(), comment.getId())
                .orElseThrow(()->  new CustomException(ErrorCode.NOT_LIKE));

        commentLikesRepository.delete(findCommentLikes);
    }

    private User getUserById(Long id) {
        return userService.findUserById(id);
    }

    private Comment getCommentById(Long id){
        return commentRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
