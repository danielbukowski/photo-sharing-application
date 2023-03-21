package com.danielbukowski.photosharing.Dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AccountDto {
    private final UUID id;
    private final String email;
    private final String password;

}
