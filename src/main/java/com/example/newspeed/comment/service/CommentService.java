package com.example.newspeed.comment.service;

import com.example.newspeed.comment.dto.CommentRequestDto;
import com.example.newspeed.comment.dto.CommentResponseDto;
import com.example.newspeed.comment.entity.Comment;
import com.example.newspeed.comment.repository.CommentRepository;
import com.example.newspeed.global.error.UnauthorizedAccessException;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.service.PostService;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    @Transactional
    public CommentResponseDto saveComment(
            Long postId,
            CommentRequestDto requestDto,
            Long currentUserId
    ) {

        User user = userService.findUserById(currentUserId);
        // id로 post를 가져옴
        Post post = postService.findPostById(postId);

        Comment comment = new Comment(user, post, requestDto.getContents());
        Comment saveComment = commentRepository.save(comment);
        return new CommentResponseDto(
                saveComment.getId(),
                saveComment.getUser().getId(),
                saveComment.getUser().getNickname(),
                saveComment.getUser().getUserUrl(),
                saveComment.getPost().getId(),
                saveComment.getContents(),
                saveComment.getCreatedAt(),
                saveComment.getModifiedAt()
        );
    }

    public List<CommentResponseDto> findAllCommentByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream().map(CommentResponseDto::toDto)
                .toList();
    }

    @Transactional
    public void updateCommnet(Long commentId, @Valid CommentRequestDto commentRequestDto, Long currentUserId) {
        User user = userService.findUserById(currentUserId);
        Comment comment= commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + commentId)
                );
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new UnauthorizedAccessException("You are not authorized to edit this comment.");
        }
        comment.updateComment(commentRequestDto);
    }
}
