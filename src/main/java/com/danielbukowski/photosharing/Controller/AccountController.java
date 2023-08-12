package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.ChangePasswordRequest;
import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Service.AccountService;
import com.danielbukowski.photosharing.Validator.Image;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal Account account,
                                           HttpServletRequest request) {
        accountService.deleteAccountById(account.getId());
        request.getSession().invalidate();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changeAccountPassword(@AuthenticationPrincipal Account account,
                                                   @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                                   HttpServletRequest request) {
        accountService.changeAccountPassword(account, changePasswordRequest);
        request.getSession().invalidate();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/images")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addImageToAccount(@AuthenticationPrincipal Account account,
                                               @Valid @Image MultipartFile image) {
        UUID imageId = accountService.saveImageToAccount(image, account);
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/api/v2/accounts/images/%s".formatted(imageId))
                                .build()
                                .toUri()
                ).build();
    }

    @GetMapping("/images/{imageId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<byte[]> getImageFromAccount(@AuthenticationPrincipal Account account,
                                                      @PathVariable UUID imageId) {
        ImageDto imageDto = accountService.getImageFromAccount(account.getId(), imageId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(imageDto.contentType()))
                .body(imageDto.data());
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteImageFromAccount(@AuthenticationPrincipal Account account,
                                                    @PathVariable UUID imageId) {
        accountService.deleteImageFromAccount(account.getId(), imageId);
        return ResponseEntity
                .noContent()
                .build();
    }

}
