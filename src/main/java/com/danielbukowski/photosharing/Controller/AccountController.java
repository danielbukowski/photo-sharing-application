package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.*;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Service.AccountService;
import com.danielbukowski.photosharing.Service.EmailVerificationTokenService;
import com.danielbukowski.photosharing.Service.ImageService;
import com.danielbukowski.photosharing.Service.PasswordResetTokenService;
import com.danielbukowski.photosharing.Validator.Image;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("api/v2/accounts")
public class AccountController {

    private final ImageService imageService;
    private final AccountService accountService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailVerificationTokenService emailVerificationTokenService;


    @GetMapping
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<?> getAccount(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(
                new SimpleDataResponse<>(accountService.getAccountDetails(account))
        );
    }

    @PutMapping
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    public ResponseEntity<?> updateAccount(@AuthenticationPrincipal Account account,
                                           @RequestBody(required = false) @Valid AccountUpdateRequest accountUpdateRequest) {
        accountService.updateAccount(account, accountUpdateRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody(required = false) @Valid AccountRegisterRequest accountRegisterRequest) {
        UUID accountId = accountService.createAccount(accountRegisterRequest);
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/%s".formatted(accountId))
                                .build()
                                .toUri()
                ).build();
    }

    @PostMapping("/email-verification")
    public ResponseEntity<?> verifyAccountByToken(@RequestParam UUID token) {
        emailVerificationTokenService.verifyEmailVerificationToken(token);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PutMapping("/email-verification")
    public ResponseEntity<?> resendEmailVerificationToken(@AuthenticationPrincipal Account account) {
        emailVerificationTokenService.resendEmailVerificationToken(account);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('USER:DELETE')")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal Account account,
                                           HttpServletRequest request) {
        accountService.deleteAccountById(account.getId());
        request.getSession().invalidate();
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/password")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    public ResponseEntity<?> changeAccountPassword(@AuthenticationPrincipal Account account,
                                                   @Valid @RequestBody(required = false) PasswordChangeRequest passwordChangeRequest) {
        accountService.changeAccountPassword(account, passwordChangeRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/images")
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<?> getImagesFromAccount(@AuthenticationPrincipal Account account,
                                                  @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        pageNumber = Integer.max(0, pageNumber);
        return ResponseEntity.ok(
                imageService.getIdsOfLatestImagesFromAccount(pageNumber, account)
        );
    }

    @PostMapping("/images")
    @PreAuthorize("hasAuthority('USER:CREATE')")
    public ResponseEntity<?> saveImageToAccount(@AuthenticationPrincipal Account account,
                                                @RequestPart(required = false) @Valid @Image MultipartFile image,
                                                @RequestPart(required = false) @Valid ImagePropertiesRequest imageProperties) {
        UUID imageId = imageService.saveImageToAccount(image, account, imageProperties);
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/api/v1/images/%s".formatted(imageId))
                                .build()
                                .toUri()
                ).build();
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasAuthority('USER:DELETE')")
    public ResponseEntity<?> deleteImageFromAccount(@AuthenticationPrincipal Account account,
                                                    @PathVariable UUID imageId) {
        imageService.deleteImageFromAccount(account.getId(), imageId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> createResetPasswordToken(@RequestBody(required = false) @Valid PasswordResetRequest passwordResetRequest) {
        passwordResetTokenService.createResetPasswordToken(passwordResetRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PutMapping("/password-reset")
    public ResponseEntity<?> changePasswordByPasswordResetTokenId(@RequestParam UUID token,
                                                                  @RequestBody(required = false) @Valid PasswordChangeRequest passwordChangeRequest) {
        passwordResetTokenService.changePasswordByPasswordResetTokenId(token, passwordChangeRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

}
