package com.danielbukowski.photosharing.Service;


import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Property.S3Properties;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class S3Service {

    private static final String IMAGES_PATH = "images/%s/%s";
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public byte[] getImageFromS3(UUID accountId, UUID imageId) {
        log.info("Getting an image with id {}", imageId);
        try {
            return s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(s3Properties.getBucketName())
                            .key(IMAGES_PATH.formatted(accountId, imageId))
                            .build()
            ).readAllBytes();
        } catch (IOException | S3Exception e) {
            log.error("Could not get an image with id {}", imageId, e);
            throw new ImageNotFoundException("Could not find an image");
        }
    }

    @Transactional
    public void saveImageToS3(UUID accountId, UUID imageId, MultipartFile image) {
        log.info("Trying to save an image with id {}", imageId);
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(s3Properties.getBucketName())
                            .key(IMAGES_PATH.formatted(accountId, imageId))
                            .build(),
                    RequestBody.fromBytes(image.getBytes())
            );
        } catch (IOException | S3Exception e) {
            log.error("Could not save an image with id {}", imageId, e);
            throw S3Exception
                    .builder()
                    .message("Could not save an image")
                    .build();
        }
    }

    @Transactional
    public void deleteImageFromS3(UUID accountId, UUID imageId) {
        log.info("Deleting an image with id {}", imageId);
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(s3Properties.getBucketName())
                            .key(IMAGES_PATH.formatted(accountId, imageId))
                            .build()
            );
        } catch (S3Exception e) {
            log.error("Could not delete an image with id {}", imageId);
            throw S3Exception
                    .builder()
                    .message("Could not delete an image")
                    .build();
        }
    }

    private List<ObjectIdentifier> getAllImagesFromS3(UUID accountId) {
        return s3Client.listObjects(
                        ListObjectsRequest.builder()
                                .bucket(s3Properties.getBucketName())
                                .prefix("images/%s".formatted(accountId))
                                .build()
                ).contents()
                .stream()
                .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                .toList();
    }

    @Transactional
    public void deleteAllImagesFromS3(UUID accountId) {
        log.info("Deleting all images from an account with id {}", accountId);
        try {
            s3Client.deleteObjects(
                    DeleteObjectsRequest.builder()
                            .delete(Delete.builder()
                                    .objects(getAllImagesFromS3(accountId))
                                    .build()
                            )
                            .bucket(s3Properties.getBucketName())
                            .build()
            );
        } catch (S3Exception e) {
            log.error("Could not save all images from an account with id {}", accountId);
            throw S3Exception
                    .builder()
                    .message("Could not delete images")
                    .build();
        }
    }

}
