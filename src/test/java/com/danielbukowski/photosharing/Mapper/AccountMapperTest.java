package com.danielbukowski.photosharing.Mapper;

import com.danielbukowski.photosharing.Entity.Account;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountMapperTest {

    private final Faker faker = new Faker();
    private final AccountMapper accountMapper = new AccountMapper();

    @Test
    void FromAccountToAccountDto_MappedFieldsAreTheSame_ReturnsEqualObject() {
        //given
        var account = Account.builder()
                .email(faker.internet().emailAddress())
                .build();

        //when
        var actualAccountDto = accountMapper.fromAccountToAccountDto(account);

        //then
        assertEquals(account.getEmail(), actualAccountDto.email());
    }

}