package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.ChangePasswordRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Role;
import com.danielbukowski.photosharing.Enum.ExceptionMessageResponse;
import com.danielbukowski.photosharing.Exception.AccountAlreadyExistsException;
import com.danielbukowski.photosharing.Exception.InvalidPasswordException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import com.danielbukowski.photosharing.Repository.RoleRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private final Faker faker = new Faker();
    @InjectMocks
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Service s3Service;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private EmailVerificationTokenService emailVerificationTokenService;

    @Test
    void CreateAccount_AccountAlreadyExists_ThrowsException() {
        //given
        var alreadyExistingEmailInDatabase = faker.internet().emailAddress();
        var accountRegisterRequest = new AccountRegisterRequest(alreadyExistingEmailInDatabase
                , "password123");
        given(accountRepository.existsByEmailIgnoreCase(alreadyExistingEmailInDatabase))
                .willReturn(true);

        //when
        var actualException = assertThrows(
                AccountAlreadyExistsException.class,
                () -> accountService.createAccount(accountRegisterRequest)
        );

        //then
        assertEquals(ExceptionMessageResponse.ACCOUNT_WITH_ALREADY_EXISTING_EMAIL.getMessage(),
                actualException.getMessage()
        );
        then(accountRepository).should(times(0))
                .save(any(Account.class));
    }

    @Test
    void CreateAccount_AccountDoesNotExist_ReturnsId() {
        //given
        var email = faker.internet().emailAddress();
        var role = new Role();
        role.setName("USER");
        var password = faker.internet().password();
        var accountRegisterRequest = new AccountRegisterRequest(email, password);
        given(roleRepository.getByName("USER"))
                .willReturn(role);

        given(accountRepository.save(any(Account.class))
        ).willReturn(Account.builder()
                .id(new UUID(1, 1))
                .password(password)
                .email(email)
                .build()
        );


        //when
        var actualId = accountService.createAccount(accountRegisterRequest);

        //then
        assertEquals(
                new UUID(1, 1),
                actualId
        );
    }

    @Test
    void DeleteAccountById_AccountExists_DeletesAccount() {
        //given
        var accountId = new UUID(1, 1);

        //when
        accountService.deleteAccountById(accountId);

        //then
        then(imageRepository).should(times(1))
                .deleteByAccountId(accountId);
        then(accountRepository).should(times(1))
                .deleteById(accountId);
        then(s3Service).should(times(1))
                .deleteAllImagesFromS3WithAccountId(accountId);
    }

    @Test
    void ChangeAccountPassword_PasswordsAreTheSame_ThrowsException() {
        //given
        var account = Account.builder()
                .password(faker.internet().password())
                .email(faker.internet().emailAddress())
                .build();
        var changePasswordRequest = new ChangePasswordRequest(account.getPassword());
        given(passwordEncoder.matches(
                changePasswordRequest.newPassword(), account.getPassword())
        ).willReturn(true);

        //when
        var actualException = assertThrowsExactly(
                InvalidPasswordException.class,
                () -> accountService.changeAccountPassword(account, changePasswordRequest)
        );

        //then
        assertEquals(
                ExceptionMessageResponse.PASSWORD_SHOULD_NOT_BE_THE_SAME.getMessage(),
                actualException.getMessage()
        );
        then(accountRepository).should(times(0))
                .updatePasswordById(any(), any());
    }

    @Test
    void ChangeAccountPassword_PasswordAreDifferent_ChangesAccountPassword() {
        //given
        var account = Account.builder()
                .id(new UUID(1, 1))
                .password(faker.internet().password())
                .email(faker.internet().emailAddress())
                .build();
        var changePasswordRequest = new ChangePasswordRequest(account.getPassword());
        given(passwordEncoder.matches(changePasswordRequest.newPassword(),
                account.getPassword())
        ).willReturn(false);
        given(passwordEncoder.encode(eq(account.getPassword()))
        ).willReturn(account.getPassword());

        //when
        assertDoesNotThrow(
                () -> accountService.changeAccountPassword(account, changePasswordRequest)
        );

        //then
        then(accountRepository).should(times(1))
                .updatePasswordById(anyString(), any(UUID.class));
    }


}