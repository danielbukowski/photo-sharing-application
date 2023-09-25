package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.EmailVerificationToken;
import com.danielbukowski.photosharing.Exception.InvalidTokenException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.EmailVerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailVerificationTokenService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final Clock clock;
    private final AccountRepository accountRepository;

    @Transactional
    public UUID createEmailVerificationTokenToAccount(Account account) {
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
                .orElseThrow(
                        () -> new InvalidTokenException("Invalid email verification token")
                );
        var accountFromToken = emailVerificationToken.getAccount();

        if (accountFromToken.isEmailVerified())
            throw new InvalidTokenException("An account has been already verified");

        if (emailVerificationToken.getExpirationDate().isBefore(LocalDateTime.now(clock)))
            throw new InvalidTokenException("This token has already expired");

        accountFromToken.setEmailVerified(true);
        emailService.sendEmailForCompletedRegistration(accountFromToken.getEmail());
    }

    @Transactional
    public void resendEmailVerificationToken(Account account) {
        if (accountRepository.isAccountEmailVerified(account.getEmail()))
            throw new InvalidTokenException("An account has been already verified");

        emailVerificationTokenRepository.findByAccountId(account.getId())
                .ifPresent(emailVerificationTokenRepository::delete);

        var token = createEmailVerificationTokenToAccount(account);
        emailService.sendEmailVerificationMessage(account.getEmail(), token);
    }
}
