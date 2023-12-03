package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.NotBlank;

public record NewCommentRequest(
        @NotBlank(message = "Not be blank") String content) {
}
