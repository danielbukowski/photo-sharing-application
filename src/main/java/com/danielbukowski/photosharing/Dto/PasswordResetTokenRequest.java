package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record PasswordResetTokenRequest(
        @NotBlank(message = "Not be blank")
        @Length(min = 8, max = 32, message = "At least 8 to 32 characters long")
        @Pattern.List({
                @Pattern(regexp = ".*[a-z].*", message = "At least one lowercase letter"),
                @Pattern(regexp = ".*[A-Z].*", message = "At least one uppercase letter"),
                @Pattern(regexp = ".*[@$!%*?&].*", message = "At least one special character(@$!%*?&)"),
                @Pattern(regexp = ".*\\d.*", message = "At least one digit from 1 to 9"),
        })
        String newPassword) {
}
