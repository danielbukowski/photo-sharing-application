package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.NotBlank;

public record NewCommentRequest(
        @NotBlank(message = "Should not be empty") String content) {
}
