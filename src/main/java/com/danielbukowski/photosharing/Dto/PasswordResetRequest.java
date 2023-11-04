package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record PasswordResetRequest(
        @NotBlank(message = "Should not be blank")
        @Length(min = 8, max = 32, message = "Should be 8-32 characters long")
        @Pattern.List({
                @Pattern(regexp = ".*[a-z].*", message = "Should have one lowercase letter"),
                @Pattern(regexp = ".*[A-Z].*", message = "Should have one uppercase letter"),
                @Pattern(regexp = ".*[@$!%*?&].*", message = "Should have one special character(@$!%*?&)"),
                @Pattern(regexp = ".*\\d.*", message = "Should have one digit from 1 to 9"),
        })
        String newPassword) {
}
