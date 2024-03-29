package com.danielbukowski.photosharing.Mapper;

import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Comment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void FromCommentToCommentDto_MapsObject_ReturnsEqualObject() {
        //given
        var comment = Comment.builder()
                .content("awesome")
                .account(Account.builder()
                        .email("myemail@mail.com")
                        .build())
                .build();

        //when
        var actualCommentDto=  commentMapper.fromCommentToCommentDto(comment);

        //then
        assertEquals(comment.getContent(), actualCommentDto.content());
        assertEquals(comment.getAccount().getNickname(), actualCommentDto.wroteBy());
        assertEquals(comment.getId(), actualCommentDto.commentId());
    }

}