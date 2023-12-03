package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record AccountRegisterRequest(
        @NotBlank(message = "Not be blank")
        @Email(message = "The email address is not a well formed e-mail address")
        String email,
        @NotBlank(message = "Not be blank")
        @Pattern(regexp = "(.*[a-zA-z].*){4}", message = "At least 4 letters")
        String nickname,
        @NotBlank(message = "Not be blank")
        @Length(min = 8, max = 32, message = "At least 8 characters to but not more than 32")
        @Pattern.List({
                @Pattern(regexp = ".*[a-z].*", message = "At least one lowercase letter"),
                @Pattern(regexp = ".*[A-Z].*", message = "At least one uppercase letter"),
                @Pattern(regexp = ".*[@$!%*?&].*", message = "At least one special character(@$!%*?&)"),
                @Pattern(regexp = ".*\\d.*", message = "At least one digit from 1 to 9"),
        }) String password) {
}
