package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.PasswordResetRequest;
import com.danielbukowski.photosharing.Dto.PasswordResetTokenRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.PasswordResetToken;
import com.danielbukowski.photosharing.Exception.AccountNotFoundException;
import com.danielbukowski.photosharing.Exception.InvalidTokenException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.PasswordResetTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.danielbukowski.photosharing.Enum.ExceptionMessageResponse.ACCOUNT_NOT_FOUND;

@Service
@AllArgsConstructor
@Slf4j
public class PasswordResetTokenService {

    private static final long TOKEN_EXPIRATION_IN_HOURS = 1;
    private final Clock clock;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createPasswordResetToken(PasswordResetRequest passwordResetRequest) {
        Account account = accountRepository.findByEmailIgnoreCase(passwordResetRequest.email())
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND.getMessage()));

        log.info("Creating a password reset token to an account with an email {}", passwordResetRequest.email());
        PasswordResetToken savedToken = passwordResetTokenRepository.save(
                PasswordResetToken.builder()
                        .expirationDate(LocalDateTime.now(clock).plusHours(TOKEN_EXPIRATION_IN_HOURS))
                        .account(account)
                        .build()
        );

        emailService.sendEmailForPasswordReset(
                account.getEmail(),
                account.getNickname(),
                savedToken.getId()
        );
    }

    @Transactional
    public void changePasswordByPasswordResetTokenId(UUID passwordResetTokenId, PasswordResetTokenRequest passwordResetTokenRequest) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findById(passwordResetTokenId)
                .orElseThrow(() -> new InvalidTokenException("The provided token is not valid"));

        if (passwordResetToken.isAlreadyUsed() || passwordResetToken.getExpirationDate().isBefore(LocalDateTime.now(clock)))
            throw new InvalidTokenException("The token has expired");

        log.info("Changing an account password with a password reset token {}", passwordResetToken);
        passwordResetToken.setAlreadyUsed(true);
        Account account = passwordResetToken.getAccount();
        account.setPassword(passwordEncoder.encode(passwordResetTokenRequest.newPassword()));

        emailService.sendEmailForPasswordResetNotification(
                account.getEmail(),
                account.getNickname()
        );
    }
}
