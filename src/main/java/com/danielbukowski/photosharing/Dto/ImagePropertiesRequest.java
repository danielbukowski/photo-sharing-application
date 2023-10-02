package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImagePropertiesRequest(
        @NotNull(message = "Should not be null") Boolean isPrivate,
        @NotBlank(message = "Should not be blank") String title) {
}
