package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetEmailRequest(
        @Email(message = "The email address is not a well formed e-mail address")
        @NotBlank(message = "Not be blank")
        String email) {
}
