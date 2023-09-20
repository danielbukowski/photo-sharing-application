package com.danielbukowski.photosharing.Service;


import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Dto.ImagePropertiesRequest;
import com.danielbukowski.photosharing.Dto.SimplePageResponse;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Image;
import com.danielbukowski.photosharing.Exception.ImageException;
import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Mapper.ImageMapper;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import com.danielbukowski.photosharing.Util.EncryptionUtils;
import com.danielbukowski.photosharing.Util.ImageUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.danielbukowski.photosharing.Enum.ExceptionMessageResponse.IMAGE_NOT_FOUND;

@Service
@Slf4j
@AllArgsConstructor
public class ImageService {

    private static final int PAGE_SIZE = 20;
    private final Clock clock;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final S3Service s3Service;
    private final ImageUtils imageUtils;
    private final EncryptionUtils encryptionUtils;

    @Cacheable(cacheNames = "images", key = "#imageId", unless = "#result.isPrivate")
    public ImageDto getImageById(UUID imageId, Account account) {
        var image = imageRepository.findById(imageId)
                .orElseThrow(
                        () -> new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage())
                );

        if (!imageUtils.hasAccessToImage(account, image))
            throw new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage());

        var imageData =
                imageUtils.decompressImage(
                        encryptionUtils.decrypt(
                                s3Service.getImageFromS3(
                                        image.getAccount().getId(),
                                        image.getId()
                                )
                        )
                );

        return imageMapper.fromImageToImageDto(
                imageData,
                image
        );
    }

    @SneakyThrows
    @Transactional
    public UUID saveImageToAccount(MultipartFile image,
                                   Account account,
                                   ImagePropertiesRequest imageProperties) {
        log.info("Saving an image to an account with an email {}", account.getEmail());
        Image savedImageWithId = imageRepository.save(Image.builder()
                .title(imageProperties.title())
                .contentType(image.getContentType())
                .creationDate(LocalDateTime.now(clock))
                .isPrivate(imageProperties.isPrivate())
                .account(account)
                .build());

        byte[] decompressedImage =
                encryptionUtils.encrypt(
                        imageUtils.compressImage(
                                image.getBytes()
                        )
                );

        s3Service.saveImageToS3(
                account.getId(),
                savedImageWithId.getId(),
                decompressedImage
        );
        return savedImageWithId.getId();
    }

    @CacheEvict(cacheNames = "images", key = "#imageId")
    @Transactional
    public void deleteImageFromAccount(UUID accountId, UUID imageId) {
        log.info("Deleting an image with id {} in an account with id {}", imageId, accountId);
        imageRepository.deleteByImageIdAndAccountId(imageId, accountId);
        s3Service.deleteImageFromS3(accountId, imageId);
    }

    public SimplePageResponse<UUID> getIdsOfLatestImages(Integer pageNumber) {
        var pageOfImages = imageRepository.findAll(
                PageRequest.of(
                        pageNumber,
                        PAGE_SIZE,
                        Sort.by(Sort.Direction.DESC, "creationDate")
                )
        );

        return new SimplePageResponse<>(
                pageOfImages.getNumberOfElements(),
                pageOfImages
                        .getContent()
                        .stream()
                        .map(Image::getId)
                        .toList(),
                pageOfImages.getNumber(),
                pageOfImages.getTotalPages(),
                pageOfImages.isLast()
        );
    }

    public SimplePageResponse<UUID> getIdsOfLatestImagesFromAccount(Integer pageNumber, Account account) {
        var pageOfImages = imageRepository.findAllByAccountId(
                PageRequest.of(
                        pageNumber,
                        PAGE_SIZE,
                        Sort.by(Sort.Direction.DESC, "creationDate")
                ),
                account.getId()
        );

        return new SimplePageResponse<>(
                pageOfImages.getNumberOfElements(),
                pageOfImages
                        .getContent()
                        .stream()
                        .map(Image::getId)
                        .toList(),
                pageOfImages.getNumber(),
                pageOfImages.getTotalPages(),
                pageOfImages.isLast()
        );
    }

    @Transactional
    public void addLikeToImage(UUID imageId, Account account) {
        var image = imageRepository.findById(imageId)
                .orElseThrow(
                        () -> new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage())
                );

        if (!imageUtils.hasAccessToImage(account, image))
            throw new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage());

        if(image.getLikes().stream().anyMatch(a -> a.equals(account)))
            throw new ImageException("You have already liked this image");

        image.getLikes().add(account);
    }

    public int getNumberOfLikesFromImage(UUID imageId, Account account) {
        var image = imageRepository.findById(imageId)
                .orElseThrow(
                        () -> new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage())
                );

        if (!imageUtils.hasAccessToImage(account, image))
            throw new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage());

        return image.getLikes().size();
    }

    @Transactional
    public void removeLikeFromImage(UUID imageId, Account account) {
        var image = imageRepository.findById(imageId)
                .orElseThrow(
                        () -> new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage())
                );

        if (!imageUtils.hasAccessToImage(account, image))
            throw new ImageNotFoundException(IMAGE_NOT_FOUND.getMessage());

        image.getLikes().removeIf((a) -> a.getId().equals(account.getId()));
    }

}
