package com.danielbukowski.photosharing.Account;

import com.danielbukowski.photosharing.Dto.AccountDto;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Mapper.AccountMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountMapperUnitTest {

    @Test
    void shouldMapAccountToAccountDto() {
        //given
        AccountMapper accountMapper = new AccountMapper();
        var account = new Account();
        account.setId(
                new UUID(0,0)
        );
        account.setEmail("test@gmail.com");
        account.setPassword("password");
        var expectedAccountDto = AccountDto.builder()
                .id(account.getId())
                .email(account.getEmail())
                .build();

        //when
        var actualAccountDto = accountMapper.fromAccountToAccountDto(account);

        //then
        assertEquals(expectedAccountDto, actualAccountDto);
    }
}