package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.ChangePasswordRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Exception.AccountAlreadyExistsException;
import com.danielbukowski.photosharing.Exception.InvalidPasswordException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import com.danielbukowski.photosharing.Repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.danielbukowski.photosharing.Enum.ExceptionMessageResponse.ACCOUNT_WITH_ALREADY_EXISTING_EMAIL;
import static com.danielbukowski.photosharing.Enum.ExceptionMessageResponse.PASSWORD_SHOULD_NOT_BE_THE_SAME;

@AllArgsConstructor
@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final EmailVerificationTokenService emailVerificationTokenService;

    @Transactional
    public UUID createAccount(AccountRegisterRequest accountRegisterRequest) {
        log.info("Creating an account with an email {}", accountRegisterRequest.email());
        if (accountRepository.existsByEmailIgnoreCase(accountRegisterRequest.email())) {
            log.error("Found an already existing account in the database with an email {}", accountRegisterRequest.email());
            throw new AccountAlreadyExistsException(
                    ACCOUNT_WITH_ALREADY_EXISTING_EMAIL.getMessage()
            );
        }

        Account accountToSave = new Account();
        accountToSave.setEmail(accountRegisterRequest.email());
        accountToSave.setPassword(passwordEncoder.encode(accountRegisterRequest.password().trim()));
        accountToSave.addRole(roleRepository.getByName("USER"));
        Account savedAccount = accountRepository.save(accountToSave);

        var emailVerificationToken = emailVerificationTokenService.createEmailVerificationTokenToAccount(savedAccount);
        emailService.sendEmailVerificationMessage(accountRegisterRequest.email(), emailVerificationToken);

        return savedAccount.getId();
    }

    @Transactional
    public void deleteAccountById(UUID accountId) {
        log.info("Deleting an account with id {}", accountId);
        imageRepository.deleteByAccountId(accountId);
        accountRepository.deleteById(accountId);
        s3Service.deleteAllImagesFromS3WithAccountId(accountId);
    }

    @Transactional
    public void changeAccountPassword(Account account, ChangePasswordRequest changePasswordRequest) {
        log.info("Changing an account password with an email {}", account.getEmail());
        String oldEncodedPassword = account.getPassword();
        String newPassword = changePasswordRequest.newPassword();

        if (passwordEncoder.matches(newPassword, oldEncodedPassword)) {
            log.error("Failed try to change an account password with an email {}", account.getEmail());
            throw new InvalidPasswordException(
                    PASSWORD_SHOULD_NOT_BE_THE_SAME.getMessage()
            );
        }

        accountRepository.updatePasswordById(passwordEncoder.encode(newPassword), account.getId());
    }

}
