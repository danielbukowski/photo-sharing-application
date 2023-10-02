package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.EmailVerificationToken;
import com.danielbukowski.photosharing.Exception.InvalidTokenException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.EmailVerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class EmailVerificationTokenService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final Clock clock;
    private final AccountRepository accountRepository;

    @Transactional
    public UUID createEmailVerificationTokenToAccount(Account account) {
        log.info("Creating an email verification token");
        return emailVerificationTokenRepository.save(
                        EmailVerificationToken
                                .builder()
                                .account(account)
                                .expirationDate(LocalDateTime.now(clock).plusHours(4L))
                                .build())
                .getId();
    }

    @Transactional
    public void verifyEmailVerificationToken(UUID emailVerificationTokenId) {
        var emailVerificationToken = emailVerificationTokenRepository.findById(emailVerificationTokenId)
                .orElseThrow(() -> new InvalidTokenException("Invalid email verification token"));
        var accountFromToken = emailVerificationToken.getAccount();

        if (accountFromToken.isEmailVerified())
            throw new InvalidTokenException("An account has been already verified");

        if (emailVerificationToken.getExpirationDate().isBefore(LocalDateTime.now(clock)))
            throw new InvalidTokenException("This token has already expired");

        log.info("Verifying an account by an email verification token {}", emailVerificationTokenId);
        accountFromToken.setEmailVerified(true);
        emailVerificationToken.setVerifiedAt(LocalDateTime.now(clock));

        emailService.sendEmailForCompletedRegistration(
                accountFromToken.getEmail(),
                accountFromToken.getNickname()
        );
    }

    @Transactional
    public void resendEmailVerificationToken(Account account) {
        if (accountRepository.isAccountEmailVerified(account.getEmail()))
            throw new InvalidTokenException("An account has been already verified");

        emailVerificationTokenRepository.findByAccountId(account.getId())
                .ifPresent(emailVerificationTokenRepository::delete);

        log.info("Creating an email verification token to account with an email {}", account.getEmail());
        var token = createEmailVerificationTokenToAccount(account);
        emailService.sendEmailForEmailVerification(
                account.getEmail(),
                account.getNickname(),
                token
        );
    }
}
