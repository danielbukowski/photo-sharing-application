package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.AccountDto;
import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.AccountUpdateRequest;
import com.danielbukowski.photosharing.Dto.PasswordChangeRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Exception.AccountAlreadyExistsException;
import com.danielbukowski.photosharing.Exception.InvalidPasswordException;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.EmailVerificationTokenRepository;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import com.danielbukowski.photosharing.Repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Transactional
    public UUID createAccount(AccountRegisterRequest accountRegisterRequest) {
        if (accountRepository.existsByEmailIgnoreCase(accountRegisterRequest.email()))
            throw new AccountAlreadyExistsException(ACCOUNT_WITH_ALREADY_EXISTING_EMAIL.getMessage());

        log.info("Creating an account with an email {}", accountRegisterRequest.email());
        Account accountToSave = new Account();
        accountToSave.setNickname(accountRegisterRequest.nickname());
        accountToSave.setLocked(false);
        accountToSave.setEmail(accountRegisterRequest.email());
        accountToSave.setPassword(passwordEncoder.encode(accountRegisterRequest.password().trim()));
        accountToSave.addRole(roleRepository.getByName("USER"));
        Account savedAccount = accountRepository.save(accountToSave);

        var emailVerificationToken = emailVerificationTokenService.createEmailVerificationTokenToAccount(savedAccount);
        emailService.sendEmailForEmailVerification(
                accountRegisterRequest.email(),
                accountRegisterRequest.nickname(),
                emailVerificationToken
        );
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
    public void changeAccountPassword(Account account, PasswordChangeRequest passwordChangeRequest) {
        log.info("Changing an account password with an email {}", account.getEmail());
        String oldPasswordFromAccount = account.getPassword();
        String newPasswordFromRequest = passwordChangeRequest.newPassword();

        if (!passwordEncoder.matches(oldPasswordFromAccount, passwordChangeRequest.oldPassword()))
            throw new InvalidPasswordException("The old password does not match the account password");

        if (passwordEncoder.matches(oldPasswordFromAccount, newPasswordFromRequest))
            throw new InvalidPasswordException(PASSWORD_SHOULD_NOT_BE_THE_SAME.getMessage());

        accountRepository.updatePasswordById(passwordEncoder.encode(newPasswordFromRequest), account.getId());
    }

    public AccountDto getAccountDetails(Account account) {
        log.info("Getting details of an account with an email {}", account.getEmail());
        var accountDetails = AccountDto.builder();

        if (account.isEmailVerified()) {
            accountDetails.accountVerifiedAt(
                    emailVerificationTokenRepository.findByAccountId(
                            account.getId()).get().getVerifiedAt()
            );
        }

        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        accountDetails.isEmailVerified(account.isEmailVerified())
                .accountId(account.getId())
                .nickname(account.getNickname())
                .email(account.getEmail())
                .biography(account.getBiography())
                .roles(roles)
                .permissions(permissions)
                .isEmailVerified(account.isEmailVerified())
                .numberOfUploadedImages(imageRepository.getImagesByAccountId(account.getId()).size())
                .isLocked(account.isLocked());

        for (GrantedAuthority authority : account.getAuthorities()) {
            if (authority.getAuthority().startsWith("ROLE_")) {
                roles.add(authority.getAuthority());
                continue;
            }
            permissions.add(authority.getAuthority());
        }

        return accountDetails.build();
    }

    @Transactional
    public void updateAccount(Account account, AccountUpdateRequest accountUpdateRequest) {
        log.info("Updating an account with an email {}", account.getEmail());
        account.setNickname(accountUpdateRequest.nickname());
        account.setBiography(accountUpdateRequest.biography());
    }

}
