package com.danielbukowski.photosharing.Mapper;

import com.danielbukowski.photosharing.Dto.CommentDto;
import com.danielbukowski.photosharing.Entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDto fromCommentToCommentDto(Comment comment) {
        return new CommentDto(comment.getContent(), comment.getAccount().getEmail());
    }
}
