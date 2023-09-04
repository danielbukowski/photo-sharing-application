package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/images")
public class ImageController {

    private final ImageService imageService;



    @GetMapping("/{imageId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<byte[]> getImageById(@PathVariable UUID imageId) {
        ImageDto imageDto = imageService.getImageById(imageId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(imageDto.contentType()))
                .body(imageDto.data());
    }
}
