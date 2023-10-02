package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.PasswordChangeRequest;
import com.danielbukowski.photosharing.Dto.PasswordResetRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.PasswordResetToken;
import com.danielbukowski.photosharing.Enum.ExceptionMessageResponse;
import com.danielbukowski.photosharing.Exception.AccountNotFoundException;
import com.danielbukowski.photosharing.Exception.InvalidTokenException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {

    @InjectMocks
    private PasswordResetTokenService passwordResetTokenService;
    @Mock
    private Clock clock;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
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
    void CreatePasswordResetToken_AccountDoesNotExist_ThrowsException() {
        //given
        var passwordResetRequest = new PasswordResetRequest("myemail3@mail.com");
        given(accountRepository.findByEmailIgnoreCase(passwordResetRequest.email()))
                .willReturn(Optional.empty());

        //when
        var actualException = assertThrows(
                AccountNotFoundException.class,
                () -> passwordResetTokenService.createPasswordResetToken(passwordResetRequest)
        );

        //then
        assertEquals(
                ExceptionMessageResponse.ACCOUNT_NOT_FOUND.getMessage(), actualException.getMessage()
        );
    }

    @Test
    void CreatePasswordResetToken_AccountExist_SendsEmail() {
        //given
        var account = Account.builder()
                .email("myemail@mail.com")
                .nickname("w00t3")
                .build();
        var passwordResetToken = PasswordResetToken.builder()
                .id(new UUID(5,5))
                .build();
        var passwordResetRequest = new PasswordResetRequest("myemail3@mail.com");
        given(accountRepository.findByEmailIgnoreCase(passwordResetRequest.email()))
                .willReturn(Optional.of(account));
        given(passwordResetTokenRepository.save(any()))
                .willReturn(passwordResetToken);
        given(clock.instant())
                .willReturn(now.toInstant());
        given(clock.getZone())
                .willReturn(now.getZone());

        //when
        passwordResetTokenService.createPasswordResetToken(passwordResetRequest);

        //then
        verify(emailService, times(1))
                .sendEmailForPasswordReset(
                        account.getEmail(),
                        account.getNickname(),
                        passwordResetToken.getId()
                );
    }

    @Test
    void ChangePasswordByPasswordResetTokenId_PasswordResetTokenDoesNotExist_ThrowsException() {
        //given
        var passwordResetTokenId = new UUID(1,1);
        var passwordChangeRequest = new PasswordChangeRequest(
                "", ""
        );
        given(passwordResetTokenRepository.findById(passwordResetTokenId))
                .willReturn(Optional.empty());

        //when
        var actualException = assertThrows(
                InvalidTokenException.class,
                () -> passwordResetTokenService.changePasswordByPasswordResetTokenId(passwordResetTokenId, passwordChangeRequest)
        );

        //then
        assertEquals("The provided token is not valid", actualException.getMessage());
    }

    @Test
    void ChangePasswordByPasswordResetTokenId_TokenIsAlreadyUsed_ThrowsException() {
        //given
        var passwordResetToken = PasswordResetToken.builder()
                .id(new UUID(1,1))
                .isAlreadyUsed(true)
                .build();
        var passwordResetTokenId = new UUID(1,1);
        var passwordChangeRequest = new PasswordChangeRequest(
                "", ""
        );
        given(passwordResetTokenRepository.findById(passwordResetTokenId))
                .willReturn(Optional.of(passwordResetToken));

        //when
        var actualException = assertThrows(
                InvalidTokenException.class,
                () -> passwordResetTokenService.changePasswordByPasswordResetTokenId(passwordResetTokenId, passwordChangeRequest)
        );

        //then
        assertEquals("The token has expired", actualException.getMessage());
    }

    @Test
    void ChangePasswordByPasswordResetTokenId_TokenIsExpired_ThrowsException() {
        //given
        when(clock.getZone()).thenReturn(now.getZone());
        when(clock.instant()).thenReturn(now.toInstant());
        var passwordResetToken = PasswordResetToken.builder()
                .id(new UUID(1,1))
                .isAlreadyUsed(false)
                .expirationDate(now.minusSeconds(1).toLocalDateTime())
                .build();
        var passwordResetTokenId = new UUID(1,1);
        var passwordChangeRequest = new PasswordChangeRequest(
                "", ""
        );
        given(passwordResetTokenRepository.findById(passwordResetTokenId))
                .willReturn(Optional.of(passwordResetToken));

        //when
        var actualException = assertThrows(
                InvalidTokenException.class,
                () -> passwordResetTokenService.changePasswordByPasswordResetTokenId(passwordResetTokenId, passwordChangeRequest)
        );

        //then
        assertEquals("The token has expired", actualException.getMessage());
    }

    @Test
    void ChangePasswordByPasswordResetTokenId_TokenIsNotExpired_SendsEmail() {
        //given
        when(clock.getZone()).thenReturn(now.getZone());
        when(clock.instant()).thenReturn(now.toInstant());
        var passwordResetToken = PasswordResetToken.builder()
                .id(new UUID(1,1))
                .isAlreadyUsed(false)
                .expirationDate(now.plusSeconds(1).toLocalDateTime())
                .account(
                        Account.builder()
                                .email("myemail@email.com")
                                .nickname("d3sc3")
                                .build()
                )
                .build();
        var passwordResetTokenId = new UUID(1,1);
        var passwordChangeRequest = new PasswordChangeRequest(
                "32kndsa!2C", "6662kndsAAA!2C"
        );
        given(passwordResetTokenRepository.findById(passwordResetTokenId))
                .willReturn(Optional.of(passwordResetToken));

        //when
        passwordResetTokenService.changePasswordByPasswordResetTokenId(passwordResetTokenId, passwordChangeRequest);

        //then
        verify(emailService, times(1))
                .sendEmailForPasswordResetNotification(
                        anyString(),
                        anyString()
                );
    }

}