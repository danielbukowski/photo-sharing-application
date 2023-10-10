package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.*;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Service.CommentService;
import com.danielbukowski.photosharing.Service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("api/v1/images")
public class ImageController {

    private final ImageService imageService;
    private final CommentService commentService;


    @Operation(
            summary = "Return an image",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "An image has been returned"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "An image does not exist"
                    )

            }
    )
    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImageById(@PathVariable UUID imageId,
                                               @AuthenticationPrincipal Account account) {
        ImageDto imageDto = imageService.getImageById(imageId, account);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(imageDto.contentType()))
                .body(imageDto.data());
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Save a comment to an image",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comment has been saved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "An image does not exist"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "An account is not email verified"
                    )
            }
    )
    @PostMapping("/{imageId}/comments")
    @PreAuthorize("hasAuthority('USER:CREATE')")
    public ResponseEntity<?> saveCommentToImage(@AuthenticationPrincipal Account account,
                                                @PathVariable UUID imageId,
                                                @Valid @RequestBody NewCommentRequest newCommentRequest) {
        commentService.saveCommentToImage(newCommentRequest, imageId, account);
        return ResponseEntity
                .created(
                        ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("api/v1/images/%s/comments".formatted(imageId))
                                .build()
                                .toUri()
                ).build();
    }

    @Operation(
            summary = "Return a page of comments from an image",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "A page of of comments have been returned"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "An image does not exist"
                    )
            }
    )
    @GetMapping("/{imageId}/comments")
    public ResponseEntity<SimplePageResponse<CommentDto>> getCommentsFromImage(@AuthenticationPrincipal Account account,
                                                                               @PathVariable UUID imageId,
                                                                               @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        pageNumber = Integer.max(0, pageNumber);
        return ResponseEntity.ok(
                commentService.getCommentsFromImage(imageId, pageNumber, account)
        );
    }

    @Operation(
            summary = "Return a page of latest images in form of ids",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "A page of latest images in form of ids have been returned"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<SimplePageResponse<UUID>> getLatestImages(@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        pageNumber = Integer.max(0, pageNumber);
        return ResponseEntity.ok(
                imageService.getIdsOfLatestImages(pageNumber)
        );
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Add a like to an image",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "A like has been added"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "An image does not exist"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "An image is already liked"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "An account is not email verified"
                    )
            }
    )
    @PostMapping("/{imageId}/likes")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    public ResponseEntity<?> addLikeToImage(@AuthenticationPrincipal Account account,
                                            @PathVariable UUID imageId) {
        imageService.addLikeToImage(imageId, account);
        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("api/v1/images/%s/likes".formatted(imageId))
                                .build()
                                .toUri()
                )
                .build();
    }

    @Operation(
            summary = "Return a number of likes of an image",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "A number of likes has been returned"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "An image does not exist"
                    )
            }
    )
    @GetMapping("/{imageId}/likes")
    public ResponseEntity<?> getNumberOfLikesFromImage(@AuthenticationPrincipal Account account,
                                                       @PathVariable UUID imageId) {
        return ResponseEntity.ok(
                new SimpleDataResponse<>(
                        Map.of("likes", imageService.getNumberOfLikesFromImage(imageId, account))
                )
        );
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Delete a like from an image",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "A like has been deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "An image does not exist"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "An account is not email verified"
                    )
            }
    )
    @DeleteMapping("/{imageId}/likes")
    @PreAuthorize("hasAuthority('USER:DELETE')")
    public ResponseEntity<?> removeLikeFromImage(@AuthenticationPrincipal Account account,
                                                 @PathVariable UUID imageId) {
        imageService.removeLikeFromImage(imageId, account);
        return ResponseEntity
                .noContent()
                .build();
    }

}
