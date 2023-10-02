package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.AccountUpdateRequest;
import com.danielbukowski.photosharing.Dto.PasswordChangeRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.EmailVerificationToken;
import com.danielbukowski.photosharing.Entity.Role;
import com.danielbukowski.photosharing.Enum.ExceptionMessageResponse;
import com.danielbukowski.photosharing.Exception.AccountAlreadyExistsException;
import com.danielbukowski.photosharing.Exception.InvalidPasswordException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.EmailVerificationTokenRepository;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import com.danielbukowski.photosharing.Repository.RoleRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final ZonedDateTime now = ZonedDateTime.of(
            2023,
            6,
            7,
            21,
            37,
            0,
            0,
            ZoneId.of("GMT")
    );

    @Test
    void CreateAccount_AccountAlreadyExists_ThrowsException() {
        //given
        var alreadyExistingEmailInDatabase = faker.internet().emailAddress();
        var accountRegisterRequest = new AccountRegisterRequest(alreadyExistingEmailInDatabase,
                "nick"
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
        var accountRegisterRequest = new AccountRegisterRequest(email,
                "nick",
                password);
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
    void ChangeAccountPassword_OldPasswordFromRequestIsNotMatchingOldPasswordFromAccount_ThrowsException() {
        //given
        var account = Account.builder().password("!L1keFood").build();
        var passwordChangeRequest = new PasswordChangeRequest("IL!ke@nimals", "myN3wPass0rd!");

        given(passwordEncoder.matches(account.getPassword(), passwordChangeRequest.oldPassword()))
                .willReturn(false);

        //when
        var actualException = assertThrows(
                InvalidPasswordException.class,
                () -> accountService.changeAccountPassword(account, passwordChangeRequest)
        );

        //then
        assertEquals("The old password does not match the account password", actualException.getMessage());
    }

    @Test
    void ChangeAccountPassword_NewPasswordFromRequestIsTheSameAsOldPasswordFromAccount_ThrowsException() {
        //given
        var account = Account.builder().password("IL!ke@nimals").build();
        var passwordChangeRequest = new PasswordChangeRequest("IL!ke@nimals", "IL!ke@nimals");
        given(passwordEncoder.matches(account.getPassword(), passwordChangeRequest.oldPassword()))
                .willReturn(true);

        given(passwordEncoder.matches(account.getPassword(), passwordChangeRequest.newPassword()))
                .willReturn(true);

        //when
        var actualException = assertThrows(
                InvalidPasswordException.class,
                () -> accountService.changeAccountPassword(account, passwordChangeRequest)
        );

        //then
        assertEquals(ExceptionMessageResponse.PASSWORD_SHOULD_NOT_BE_THE_SAME.getMessage(), actualException.getMessage());
    }

    @Test
    void ChangeAccountPassword_OldPasswordsAreTheSameAndNewPasswordIsDifferent_UpdatesPassword() {
        //given
        var account = Account.builder().password("myOldP4ss!").build();
        var passwordChangeRequest = new PasswordChangeRequest("myOldP4ss!", "IL!ke@nimals");
        given(passwordEncoder.matches(
                account.getPassword(),
                passwordChangeRequest.oldPassword()))
                .willReturn(true);

        given(passwordEncoder.matches(account.getPassword(), passwordChangeRequest.newPassword()))
                .willReturn(false);

        //when
        accountService.changeAccountPassword(account, passwordChangeRequest);

        //then
        verify(accountRepository, times(1)).updatePasswordById(any(), any());
    }

    @Test
    void GetAccountDetails_CallsTheMethod_ReturnsAccountDto() {
        //given
        var account = Account.builder()
                .email("user1@gmail.com")
                .password("password")
                .id(new UUID(1, 1))
                .isEmailVerified(true)
                .isLocked(false)
                .nickname("user1")
                .roles(
                        Set.of(
                                Role.builder()
                                        .name("USER")
                                        .permissions("USER:READ,USER:CREATE,USER:UPDATE,USER:DELETE")
                                        .build()
                        )
                )
                .build();
        given(emailVerificationTokenRepository.findByAccountId(account.getId()))
                .willReturn(Optional.of(
                        EmailVerificationToken.builder()
                                .verifiedAt(now.toLocalDateTime())
                                .build())
                );

        //when
        var actualAccountDto = accountService.getAccountDetails(account);

        //then
        assertEquals(account.getEmail(), actualAccountDto.email());
    }

    @Test
    void UpdateAccount_ChangesAccountField_ChecksChangedAccountFields() {
        //given
        var accountUpdateResult = new AccountUpdateRequest("fastRat3", ":)");
        var account = Account.builder().build();

        //when
        accountService.updateAccount(account, accountUpdateResult);

        //then
        assertEquals(accountUpdateResult.nickname(), account.getNickname());
        assertEquals(accountUpdateResult.biography(), account.getBiography());
    }

}