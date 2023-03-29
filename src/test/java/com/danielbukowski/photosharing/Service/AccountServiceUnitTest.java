package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Exception.AccountNotFoundException;
import com.danielbukowski.photosharing.Mapper.AccountMapper;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceUnitTest {


    private final Faker faker = new Faker();

    @InjectMocks
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Spy
    private PasswordEncoder passwordEncoder;
    @Spy
    private AccountMapper accountMapper;

    @Test
    void should_thrown_exception_when_register_request_contains_email_that_already_exists_in_accounts() {
        //given
        var alreadyExistingEmailInAccounts = "myemail@mail.com";
        Account account = new Account(
                new UUID(0, 0),
                alreadyExistingEmailInAccounts,
                "password123"
        );
        var accountRegisterRequest = new AccountRegisterRequest(alreadyExistingEmailInAccounts, "password123");

        //when
        when(accountRepository.findByEmailIgnoreCase(alreadyExistingEmailInAccounts))
                .thenReturn(Optional.of(account));

        var thrownException = assertThrows(RuntimeException.class, () ->
                accountService.createAccount(accountRegisterRequest)
        );

        //then
        assertEquals(
                "An account with this email already exists",
                thrownException.getMessage()
        );

    }

    @Test
    void should_return_all_account_list() {
        //given
        var account1 = new Account();
        account1.setPassword(faker.internet().password());
        account1.setEmail(faker.internet().emailAddress());
        account1.setId(new UUID(1,1));

        var account2 = new Account();
        account2.setPassword(faker.internet().password());
        account2.setEmail(faker.internet().emailAddress());
        account2.setId(new UUID(2,2));

        var accountList = List.of(account1, account2);


        var exceptedResult = accountList
                .stream()
                .map(accountMapper::fromAccountToAccountDto)
                .toList();
        //when
        when(accountRepository.findAll()).thenReturn(accountList);
        var resultAccountList = accountService.getAccounts();

        //then
        assertEquals(resultAccountList.size(), 2);
        assertTrue(resultAccountList.containsAll(exceptedResult));

    }


    @Test
    void should_throw_exception_when_account_is_not_found() {
        //given
        var id = new UUID(0, 0);


        //when
        when(accountRepository.findById(Mockito.any(UUID.class))).thenReturn(
                Optional.empty()
        );
        var resultException = assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccountById(id)
        );

        //then
        assertEquals("An account with this id doesn't exist",
                resultException.getMessage()
                );
    }

    @Test
    void should_thrown_exception_when_method_delete_by_account_id_is_called_and_account_with_id_doesnt_exist() {
        //given
        var id = new UUID(1,1);


        //when
        var resultException = assertThrows(AccountNotFoundException.class,
                () -> accountService.deleteAccountById(id));
        //then
        assertEquals("An account with this id doesn't exist", resultException.getMessage());
    }


}