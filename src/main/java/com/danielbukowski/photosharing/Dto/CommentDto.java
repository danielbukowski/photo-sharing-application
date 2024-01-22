package com.danielbukowski.photosharing.Dto;

import java.util.UUID;

public record CommentDto(
        UUID commentId,
        String content,
        String nickname
        ) {
}
