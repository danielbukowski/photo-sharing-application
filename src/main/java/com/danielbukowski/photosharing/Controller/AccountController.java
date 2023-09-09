package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.ChangePasswordRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Service.AccountService;
import com.danielbukowski.photosharing.Service.EmailVerificationTokenService;
import com.danielbukowski.photosharing.Service.ImageService;
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

    private final AccountService accountService;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountRegisterRequest accountRegisterRequest) {
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal Account account,
                                           HttpServletRequest request) {
        accountService.deleteAccountById(account.getId());
        request.getSession().invalidate();
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changeAccountPassword(@AuthenticationPrincipal Account account,
                                                   @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                                   HttpServletRequest request) {
        accountService.changeAccountPassword(account, changePasswordRequest);
        request.getSession().invalidate();
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/images")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> saveImageToAccount(@AuthenticationPrincipal Account account,
                                                @Valid @Image MultipartFile image) {
        UUID imageId = imageService.saveImageToAccount(image, account);
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteImageFromAccount(@AuthenticationPrincipal Account account,
                                                    @PathVariable UUID imageId) {
        imageService.deleteImageFromAccount(account.getId(), imageId);
        return ResponseEntity
                .noContent()
                .build();
    }

}
