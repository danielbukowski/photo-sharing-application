package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImagePropertiesRequest(
        @NotNull(message = "Not be null") Boolean isPrivate,
        @NotBlank(message = "Not be blank") String title) {
}
