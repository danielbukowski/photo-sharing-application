package com.danielbukowski.photosharing.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountRegisterRequest {

    @NotEmpty(message = "Email should not be empty")
    @Email
    private final String email;
    @NotEmpty(message = "Password should not be empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$",
            message = "Password must contain one digit from 1 to 9, " +
                    "one lowercase letter, " +
                    "one uppercase letter, " +
                    "one special character, " +
                    "no space, " +
                    "and it must be 8-32 characters long.")
    private final String password;

}
