package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.AccountDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @GetMapping("{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountRegisterRequest accountRegisterRequest) {
        UUID accountId = accountService.createAccount(accountRegisterRequest);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/" + accountId).build().toUri())
                .build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteAccountById(@PathVariable UUID id) {
        accountService.deleteAccountById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(Authentication authentication,
                                            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        accountService.changePassword(authentication.getName(), changePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/images")
    public ResponseEntity<?> addImageToAccount(@Valid @Image MultipartFile image,
                                               @AuthenticationPrincipal Account account) {
        UUID imageId = accountService.saveImageToAccount(image, account.getUsername());
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/api/v1/accounts/images/%s".formatted(imageId))
                                .build()
                                .toUri()
                ).build();
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImageFromAccount(@PathVariable UUID imageId,
                                                      @AuthenticationPrincipal Account account) {
        ImageDto imageDto = accountService.getImageFromAccount(account, imageId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(
                        MediaType.parseMediaType("image/%s".formatted(imageDto.extension()))
                )
                .body(imageDto.data());
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImageFromAccount(@PathVariable UUID imageId,
                                                    @AuthenticationPrincipal Account account) {
        accountService.deleteImageFromAccount(account, imageId);
        return ResponseEntity
                .noContent()
                .build();
    }


}
