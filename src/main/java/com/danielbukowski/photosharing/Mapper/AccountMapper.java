package com.danielbukowski.photosharing.Mapper;

import com.danielbukowski.photosharing.Dto.AccountDto;
import com.danielbukowski.photosharing.Entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDto fromAccountToAccountDto(Account account) {
        return AccountDto.builder()
                .email(account.getEmail())
                .build();
    }

}
