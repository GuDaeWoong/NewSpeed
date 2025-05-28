package com.example.newspeed.comment.service;

import com.example.newspeed.comment.dto.CommentRequestDto;
import com.example.newspeed.comment.dto.CommentResponseDto;
import com.example.newspeed.comment.entity.Comment;
import com.example.newspeed.comment.repository.CommentRepository;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.service.PostService;
import com.example.newspeed.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

//    public CommentResponseDto saveComment(
//            Long postId,
//            CommentRequestDto requestDto,
//            currentUserId
//    ) {
//        Post postGetId =(Post) postService.getId(requestDto1);
//        Comment comment = new Comment(currentUserId, postGetId, requestDto.getContents());
//        Comment saveComment = commentRepository.save(comment);
//        CommentResponseDto responseDto = new CommentResponseDto(
//                saveComment.getId(),
//                saveComment.getUser().getId(),
//                saveComment.getPost().getId(),
//                saveComment.getContents(),
//                saveComment.getCreatedAt(),
//                saveComment.getModifiedAt()
//        );
//        return responseDto;
//    }

    public List<CommentResponseDto> findAllCommentByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream().map(CommentResponseDto::toDto)
                .toList();
    }


}
