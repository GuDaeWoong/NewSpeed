package com.example.newspeed.comment.service;

import com.example.newspeed.comment.dto.CommentRequestDto;
import com.example.newspeed.comment.dto.CommentResponseDto;
import com.example.newspeed.comment.dto.CommentWithLikesDto;
import com.example.newspeed.comment.dto.DeleteCommentDto;
import com.example.newspeed.comment.entity.Comment;
import com.example.newspeed.comment.repository.CommentRepository;
import com.example.newspeed.global.Enums.ErrorCode;
import com.example.newspeed.global.config.SecurityConfig;
import com.example.newspeed.global.error.CustomException;
import com.example.newspeed.post.entity.Post;
import com.example.newspeed.post.service.PostService;
import com.example.newspeed.user.entity.User;
import com.example.newspeed.user.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;
    private final SecurityConfig securityConfig;


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

    public Page<CommentWithLikesDto> findAllCommentByPostId(Long postId, Pageable pageable) {
        // Pageable의 페이지 번호가 1부터 시작한다고 가정하고, 0부터 시작하는 Pageable로 변환
        int pageNumber = pageable.getPageNumber()-1 ;
        // 요청 페이지 번호가 음수가 되는 것을 방지
        if (pageNumber < 0) {
            pageNumber = 0;
        }

        Pageable adjustedPageable = PageRequest.of(pageNumber, pageable.getPageSize(), pageable.getSort());
        Page<CommentWithLikesDto> commentPage = commentRepository.findCommentsWithLikeCountByPostId(postId, adjustedPageable);
        if (commentPage.isEmpty() && pageNumber >= commentPage.getTotalPages()) {
            throw new CustomException(ErrorCode.PAGE_NOT_FOUND);
        }

        return commentPage;
    }

    @Transactional
    public void updateCommnet(Long commentId, @Valid CommentRequestDto commentRequestDto, Long currentUserId) {
        User user = userService.findUserById(currentUserId);
        Comment comment= commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new CustomException(ErrorCode.COMMENT_NOT_OWNED);
        }
        comment.updateComment(commentRequestDto);
    }

    @Transactional
    public void deleteComment(Long commentId, DeleteCommentDto deleteDto, Long currentUserId) {
        User user = userService.findUserById(currentUserId);
        Comment comment= commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!user.getId().equals(comment.getUser().getId())) {
            throw new CustomException(ErrorCode.COMMENT_NOT_OWNED);
        }

        if (!securityConfig.passwordEncoder().matches(deleteDto.getPassword(),user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        commentRepository.delete(comment);

    }
}
