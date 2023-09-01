package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.ChangePasswordRequest;
import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Image;
import com.danielbukowski.photosharing.Exception.AccountAlreadyExistsException;
import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Exception.InvalidPasswordException;
import com.danielbukowski.photosharing.Mapper.ImageMapper;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import com.danielbukowski.photosharing.Repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.danielbukowski.photosharing.Enum.ExceptionMessageResponse.*;

@AllArgsConstructor
@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageMapper imageMapper;
    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final Clock clock;

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
        s3Service.deleteAllImagesFromS3(accountId);
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

    @Transactional
    public UUID saveImageToAccount(MultipartFile image, Account account) {
        log.info("Saving an image to an account with an email {}", account.getEmail());
        Image savedImageWithId = imageRepository.save(Image.builder()
                .title(FilenameUtils.getBaseName(image.getOriginalFilename()))
                .contentType(image.getContentType())
                .creationDate(LocalDateTime.now(clock))
                .account(account)
                .build());

        s3Service.saveImageToS3(account.getId(), savedImageWithId.getId(), image);
        return savedImageWithId.getId();
    }

    public ImageDto getImageFromAccount(UUID accountId, UUID imageId) {
        log.info("Getting an image with id {}", imageId);
        Image image = imageRepository.findByImageIdAndAccountId(imageId, accountId)
                .orElseThrow(() -> {
                    log.error("Failed to find an image with id {}", imageId);
                    return new ImageNotFoundException(
                            IMAGE_NOT_FOUND.getMessage());
                });

        byte[] imageInBytes = s3Service.getImageFromS3(accountId, imageId);
        return imageMapper.fromImageToImageDto(imageInBytes, image);
    }

    @Transactional
    public void deleteImageFromAccount(UUID accountId, UUID imageId) {
        log.info("Deleting an image with id {}", imageId);
        s3Service.deleteImageFromS3(accountId, imageId);
        imageRepository.deleteById(imageId);
    }

}
