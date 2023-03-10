package com.danielbukowski.photosharing.Dto;

import lombok.Data;

@Data
public class AccountRegisterRequest {


    private final String login;
    private final String password;

}
