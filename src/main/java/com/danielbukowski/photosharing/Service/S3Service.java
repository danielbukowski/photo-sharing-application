package com.danielbukowski.photosharing.Service;


import com.danielbukowski.photosharing.Property.S3Properties;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;


@Service
@AllArgsConstructor
public class S3Service {

    private static final String IMAGES_PATH = "images/%s/%s";
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public byte[] getImageFromS3(UUID accountId, UUID imageId) {
        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(s3Properties.getBucketName())
                        .key(IMAGES_PATH.formatted(accountId, imageId))
                        .build()
        );
        try {
            return object.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't find an image in the account with id [%s]".formatted(accountId));
        }
    }

    public void saveImageToS3(UUID accountId, UUID imageId, MultipartFile image) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(s3Properties.getBucketName())
                            .key(IMAGES_PATH.formatted(accountId, imageId))
                            .build(),
                    RequestBody.fromBytes(image.getBytes())
            );
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save an image");
        }
    }

}
