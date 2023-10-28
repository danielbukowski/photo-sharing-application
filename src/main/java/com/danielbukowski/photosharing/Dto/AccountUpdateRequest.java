package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record AccountUpdateRequest(
        @NotBlank(message = "Should not be blank")
        @Pattern(regexp = "(.*[a-zA-z].*){4}", message = "Should have at 4 letters")
        @Length(min = 4, message = "The length should be at least 4 characters")
        String nickname,
        String biography) {
}
