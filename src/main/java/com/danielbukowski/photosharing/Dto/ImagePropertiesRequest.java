package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.NotBlank;

public record ImagePropertiesRequest(
        boolean isPrivate,
        @NotBlank(message = "Should not be blank") String title) {
}
