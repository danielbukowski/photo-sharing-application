package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.CommentDto;
import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Dto.NewCommentRequest;
import com.danielbukowski.photosharing.Dto.SimplePageResponse;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Service.CommentService;
import com.danielbukowski.photosharing.Service.ImageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImageById(@PathVariable UUID imageId,
                                               @AuthenticationPrincipal Account account) {
        ImageDto imageDto = imageService.getImageById(imageId, account);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(imageDto.contentType()))
                .body(imageDto.data());
    }

    @PostMapping("/{imageId}/comments")
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

    @GetMapping("/{imageId}/comments")
    public ResponseEntity<SimplePageResponse<CommentDto>> getCommentsFromImage(@AuthenticationPrincipal Account account,
                                                                               @PathVariable UUID imageId,
                                                                               @RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        pageNumber = Integer.max(0, pageNumber);
        return ResponseEntity.ok(commentService.getCommentsFromImage(imageId, pageNumber, account));
    }

    @GetMapping
    public ResponseEntity<SimplePageResponse<UUID>> getListOfLatestImages(@RequestParam(required = false, defaultValue = "0") Integer pageNumber) {
        pageNumber = Integer.max(0, pageNumber);
        return ResponseEntity.ok(imageService.getIdsOfLatestImages(pageNumber));
    }

    @PostMapping("/{imageId}/likes")
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

    @GetMapping("/{imageId}/likes")
    public ResponseEntity<?> getNumberOfLikesFromImage(@AuthenticationPrincipal Account account,
                                                       @PathVariable UUID imageId) {
        return ResponseEntity.ok(Map.of("likes", imageService.getNumberOfLikesFromImage(imageId, account)));
    }

    @DeleteMapping("/{imageId}/likes")
    public ResponseEntity<?> removeLikeFromImage(@AuthenticationPrincipal Account account,
                                                 @PathVariable UUID imageId) {
        imageService.removeLikeFromImage(imageId, account);
        return ResponseEntity
                .noContent()
                .build();
    }

}
