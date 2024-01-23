package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.*;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Service.AccountService;
import com.danielbukowski.photosharing.Service.EmailVerificationTokenService;
import com.danielbukowski.photosharing.Service.ImageService;
import com.danielbukowski.photosharing.Service.PasswordResetTokenService;
import com.danielbukowski.photosharing.Validator.Image;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("api/v3/accounts")
public class AccountController {

    private final ImageService imageService;
    private final AccountService accountService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailVerificationTokenService emailVerificationTokenService;

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Return details about an account",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account details are returned"
                    )
            })
    @GetMapping
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<SimpleDataResponse<AccountDto>> getAccount(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(
                new SimpleDataResponse<>(accountService.getAccountDetails(account))
        );
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Update an account",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "An account has been updated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "An account is not email verified"
                    )
            }
    )
    @PutMapping
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    public ResponseEntity<Void> updateAccount(@AuthenticationPrincipal Account account,
                                              @RequestBody @Valid AccountUpdateRequest accountUpdateRequest) {
        accountService.updateAccount(account, accountUpdateRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(
            summary = "Create an account",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "An account has been created"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Request body is not valid"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<Void> createAccount(@RequestBody @Valid AccountRegisterRequest accountRegisterRequest) {
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

    @Operation(
            summary = "Verify an account by an email verification token",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "An account has been email verified"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "An email verification token is invalid / An account is already verified"
                    )
            }
    )
    @PostMapping("/email-verification")
    public ResponseEntity<Void> verifyAccountByEmailVerificationToken(@RequestParam UUID token) {
        emailVerificationTokenService.verifyEmailVerificationToken(token);
        return ResponseEntity
                .noContent()
                .build();
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Resend an email verification token",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "An email verification token has been resent"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "An account is already email verified"
                    )
            }
    )
    @PutMapping("/email-verification")
    public ResponseEntity<Void> resendEmailVerificationToken(@AuthenticationPrincipal Account account) {
        emailVerificationTokenService.resendEmailVerificationToken(account);
        return ResponseEntity
                .noContent()
                .build();
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Delete an account",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "An account has been deleted"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "An account is not email verified"
                    )
            }
    )
    @DeleteMapping
    @PreAuthorize("hasAuthority('USER:DELETE')")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal Account account,
                                              HttpServletRequest request) {
        accountService.deleteAccountById(account.getId());
        request.getSession().invalidate();
        return ResponseEntity
                .noContent()
                .build();
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Change a password",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "A password has been changed"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "A password is invalid"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "An account is not email verified"
                    )
            }
    )
    @PatchMapping("/password")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    public ResponseEntity<Void> changeAccountPassword(@AuthenticationPrincipal Account account,
                                                      @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        accountService.changeAccountPassword(account, passwordChangeRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Return a list of owned images in form of ids",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "A list of images in form of ids have been returned"
                    )
            }
    )
    @GetMapping("/images")
    @PreAuthorize("hasAuthority('USER:READ')")
    public ResponseEntity<SimplePageResponse<UUID>> getImagesFromAccount(@AuthenticationPrincipal Account account,
                                                                         @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        pageNumber = Integer.max(0, pageNumber);
        return ResponseEntity.ok(
                imageService.getIdsOfLatestImagesFromAccount(pageNumber, account)
        );
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Save an image to an account",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "An image has been saved to an account"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "An account is not email verified"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "An account is not email verified"
                    )
            }
    )
    @PostMapping("/images")
    @PreAuthorize("hasAuthority('USER:CREATE')")
    public ResponseEntity<Void> saveImageToAccount(@AuthenticationPrincipal Account account,
                                                   @RequestPart @Valid @Image MultipartFile image,
                                                   @RequestPart @Valid ImagePropertiesRequest imageProperties) {
        UUID imageId = imageService.saveImageToAccount(image, account, imageProperties);
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/api/v2/images/%s".formatted(imageId))
                                .build()
                                .toUri()
                ).build();
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Delete an image from an account",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "An image has been deleted"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "An account is not email verified"
                    )
            }
    )
    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasAuthority('USER:DELETE')")
    public ResponseEntity<Void> deleteImageFromAccount(@AuthenticationPrincipal Account account,
                                                       @PathVariable UUID imageId) {
        imageService.deleteImageFromAccount(account.getId(), imageId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(
            summary = "Create a password reset token",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "A password reset token has been created"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "An account does not exist"
                    )
            }
    )
    @PostMapping("/password-reset")
    public ResponseEntity<Void> createPasswordResetToken(@RequestBody @Valid PasswordResetEmailRequest passwordResetEmailRequest) {
        passwordResetTokenService.createPasswordResetToken(passwordResetEmailRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(
            summary = "Change a password by a password reset token",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "A password has been changed"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid password reset token"
                    )
            }
    )
    @PutMapping("/password-reset")
    public ResponseEntity<Void> changePasswordByPasswordResetToken(@RequestParam UUID token,
                                                                   @RequestBody @Valid PasswordResetTokenRequest passwordResetTokenRequest) {
        passwordResetTokenService.changePasswordByPasswordResetTokenId(token, passwordResetTokenRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

}
