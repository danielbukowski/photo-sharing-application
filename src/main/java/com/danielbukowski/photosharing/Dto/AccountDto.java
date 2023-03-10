package com.danielbukowski.photosharing.Dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {


    private final String login;

    private final String password;

}
