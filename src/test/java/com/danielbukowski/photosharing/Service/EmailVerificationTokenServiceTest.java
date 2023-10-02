package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.EmailVerificationToken;
import com.danielbukowski.photosharing.Exception.InvalidTokenException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.EmailVerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EmailVerificationTokenServiceTest {

    @InjectMocks
    private EmailVerificationTokenService emailVerificationTokenService;
    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private Clock clock;
    @Mock
    private AccountRepository accountRepository;
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
    void VerifyEmailVerificationToken_TokenDoesNotExist_ThrowsException() {
        //given
        var token = new UUID(1, 1);
        given(emailVerificationTokenRepository.findById(token))
                .willReturn(Optional.empty());

        //when
        var expectedException = assertThrows(
                InvalidTokenException.class,
                () -> emailVerificationTokenService.verifyEmailVerificationToken(token)
        );

        //then
        assertEquals("Invalid email verification token", expectedException.getMessage());
    }

    @Test
    void VerifyEmailVerificationToken_AccountIsAlreadyVerified_ThrowsException() {
        //given
        var token = new UUID(1, 1);
        var account = Account.builder()
                .isEmailVerified(true).build();
        var emailVerificationToken = EmailVerificationToken.builder()
                .account(account)
                .build();
        given(emailVerificationTokenRepository.findById(token))
                .willReturn(Optional.of(emailVerificationToken));

        //when
        var expectedException = assertThrows(
                InvalidTokenException.class,
                () -> emailVerificationTokenService.verifyEmailVerificationToken(token)
        );

        //then
        assertEquals("An account has been already verified", expectedException.getMessage());
    }

    @Test
    void VerifyEmailVerificationToken_TokenIsExpired_ThrowsException() {
        //given
        given(clock.instant()).willReturn(now.toInstant());
        given(clock.getZone()).willReturn(now.getZone());
        var token = new UUID(1, 1);
        var account = Account.builder()
                .isEmailVerified(false).build();
        var emailVerificationToken = EmailVerificationToken.builder()
                .account(account)
                .expirationDate(now.minusSeconds(1).toLocalDateTime())
                .build();
        given(emailVerificationTokenRepository.findById(token))
                .willReturn(Optional.of(emailVerificationToken));

        //when
        var expectedException = assertThrows(
                InvalidTokenException.class,
                () -> emailVerificationTokenService.verifyEmailVerificationToken(token)
        );

        //then
        assertEquals("This token has already expired", expectedException.getMessage());
    }

    @Test
    void VerifyEmailVerificationToken_TokenExpirationEqualsToCurrentTime_VerifiesAccount() {
        //given
        given(clock.instant()).willReturn(now.toInstant());
        given(clock.getZone()).willReturn(now.getZone());
        var token = new UUID(1, 1);
        var account = Account.builder()
                .email("myemail@gmail.com")
                .nickname("c00l3")
                .isEmailVerified(false)
                .build();
        var emailVerificationToken = EmailVerificationToken.builder()
                .account(account)
                .expirationDate(now.toLocalDateTime())
                .build();
        given(emailVerificationTokenRepository.findById(token))
                .willReturn(Optional.of(emailVerificationToken));

        //when
        emailVerificationTokenService.verifyEmailVerificationToken(token);

        //then
        assertTrue(account.isEmailVerified());
        Mockito.verify(emailService, times(1)).sendEmailForCompletedRegistration(
                anyString(),
                anyString()
        );
    }

    @Test
    void VerifyEmailVerificationToken_AccountIsNotVerifiedAndTokenIsNotExpired_VerifiesAccount() {
        //given
        given(clock.instant()).willReturn(now.toInstant());
        given(clock.getZone()).willReturn(now.getZone());
        var token = new UUID(1, 1);
        var account = Account.builder()
                .email("myemail@gmail.com")
                .isEmailVerified(false)
                .nickname("co00l")
                .build();
        var emailVerificationToken = EmailVerificationToken.builder()
                .account(account)
                .expirationDate(now.plusSeconds(1).toLocalDateTime())
                .build();
        given(emailVerificationTokenRepository.findById(token))
                .willReturn(Optional.of(emailVerificationToken));

        //when
        emailVerificationTokenService.verifyEmailVerificationToken(token);

        //then
        assertTrue(account.isEmailVerified());
        Mockito.verify(emailService, times(1))
                .sendEmailForCompletedRegistration(
                anyString(),
                        anyString()
        );
    }

    @Test
    void ResendEmailVerificationToken_AccountIsAlreadyVerified_ThrowsException() {
        //given
        var account = com.danielbukowski.photosharing.Entity.Account.builder()
                .email("myemail@gmail.com")
                .build();
        given(accountRepository.isAccountEmailVerified("myemail@gmail.com"))
                .willReturn(true);

        //when
        var actualException = assertThrows(
                InvalidTokenException.class,
                () -> emailVerificationTokenService.resendEmailVerificationToken(account)
        );
        //then
        assertEquals("An account has been already verified", actualException.getMessage());
    }

    @Test
    void ResendEmailVerificationToken_AccountIsNotVerified_ResendsEmail() {
        //given
        given(clock.instant()).willReturn(now.toInstant());
        given(clock.getZone()).willReturn(now.getZone());
        var account = com.danielbukowski.photosharing.Entity.Account.builder()
                .email("myemail@gmail.com")
                .nickname("c00l3")
                .build();
        given(emailVerificationTokenRepository.save(any(EmailVerificationToken.class)))
                .willReturn(EmailVerificationToken.builder()
                        .id(new UUID(2,2))
                        .build());

        given(accountRepository.isAccountEmailVerified("myemail@gmail.com"))
                .willReturn(false);

        //when
        emailVerificationTokenService.resendEmailVerificationToken(account);

        //then
        Mockito.verify(emailService, times(1)).sendEmailForEmailVerification(
                anyString(),
                anyString(),
                Mockito.any(UUID.class)
                );
    }

}