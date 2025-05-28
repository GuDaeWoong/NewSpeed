package com.example.newspeed.comment.service;

import com.example.newspeed.comment.dto.CommentRequestDto;
import com.example.newspeed.comment.dto.CommentResponseDto;
import com.example.newspeed.comment.entity.Comment;
import com.example.newspeed.comment.repository.CommentRepository;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.service.PostService;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

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

//    public void updateCommnet(Long commentId, CommentRequestDto commentRequestDto, Long currentUserId
//    ) {
//        Optional<Comment> comment = Optional.ofNullable(commentRepository.findById(commentId)
//                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + commentId))
//        );
//
//        // 로그인한 사용자가 해당하는 댓글의 작성자 인가
//        if (comment.get().getId().equals(currentUserId)) {
//            throw new
//        }
//    }


}
