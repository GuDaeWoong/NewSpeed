package com.example.newspeed.comment.dto;

import com.example.newspeed.comment.entity.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentRequestDto {
    private String contents;

}
