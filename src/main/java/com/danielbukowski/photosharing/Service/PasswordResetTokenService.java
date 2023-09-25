package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.PasswordChangeRequest;
import com.danielbukowski.photosharing.Dto.PasswordResetRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.PasswordResetToken;
import com.danielbukowski.photosharing.Exception.AccountNotFoundException;
import com.danielbukowski.photosharing.Exception.InvalidTokenException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.PasswordResetTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.danielbukowski.photosharing.Enum.ExceptionMessageResponse.ACCOUNT_NOT_FOUND;

@Service
@AllArgsConstructor
public class PasswordResetTokenService {

    private static final long TOKEN_EXPIRATION_IN_HOURS = 1;
    private final Clock clock;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createResetPasswordToken(PasswordResetRequest passwordResetRequest) {
        Account account = accountRepository.findByEmailIgnoreCase(passwordResetRequest.email())
                .orElseThrow(
                        () -> new AccountNotFoundException(ACCOUNT_NOT_FOUND.getMessage())
                );

        PasswordResetToken savedToken = passwordResetTokenRepository.save(
                PasswordResetToken.builder()
                        .expirationDate(LocalDateTime.now(clock).plusHours(TOKEN_EXPIRATION_IN_HOURS))
                        .account(account)
                        .build()
        );

        emailService.sendEmailWithResetPasswordToken(account.getEmail(),
                savedToken.getId(),
                account.getNickname()
        );
    }

    @Transactional
    public void changePasswordByPasswordResetTokenId(UUID passwordResetTokenId, PasswordChangeRequest passwordChangeRequest) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findById(passwordResetTokenId)
                .orElseThrow(
                        () -> new InvalidTokenException(
                                "The provided token is not valid"
                        )
                );

        if(passwordResetToken.isAlreadyUsed() || passwordResetToken.getExpirationDate().isBefore(LocalDateTime.now(clock))) {
            throw new InvalidTokenException("The token has expired");
        }

        passwordResetToken.setAlreadyUsed(true);
        Account account = passwordResetToken.getAccount();
        account.setPassword(passwordEncoder.encode(passwordChangeRequest.newPassword()));

        emailService.sendPasswordResetNotification(
                account.getEmail(),
                account.getNickname()
        );
    }
}
