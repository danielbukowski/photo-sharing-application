package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetEmailRequest(
        @Email
        @NotBlank(message = "Not be blank")
        String email) {
}
