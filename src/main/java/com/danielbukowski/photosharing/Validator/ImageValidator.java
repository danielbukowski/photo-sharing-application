package com.danielbukowski.photosharing.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
public class ImageValidator implements ConstraintValidator<Image, MultipartFile> {

    public static final String JPEG = "JPEG";
    public static final String PNG = "PNG";
    public static final String JPG = "JPG";

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null) return false;

        return isFileExtensionValid(file)
                && isContentTypeValid(file)
                && isFileTypeValid(file);
    }

    private boolean isFileExtensionValid(MultipartFile multipartFile) {
        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        log.info("Found a file with a file extension {}", fileExtension);
        return JPEG.equalsIgnoreCase(fileExtension)
                || JPG.equalsIgnoreCase(fileExtension)
                || PNG.equalsIgnoreCase(fileExtension);
    }

    private boolean isContentTypeValid(MultipartFile multipartFile) {
        String fileContentType = multipartFile.getContentType();
        log.info("Found a file with a content type {}", fileContentType);
        return Objects.equals(fileContentType, MediaType.IMAGE_JPEG_VALUE)
                || Objects.equals(fileContentType, MediaType.IMAGE_PNG_VALUE)
                ;
    }

    @SneakyThrows
    private boolean isFileTypeValid(MultipartFile multipartFile) {
        String fileType = getFileType(multipartFile.getBytes());
        log.info("Found a file with a file type {}", fileType);
        return fileType != null
                && (fileType.equals(PNG)
                || fileType.equals(JPEG));
    }

    // Checks Magic Bytes in a file
    private String getFileType(byte[] fileData) {
        // Finds the '‰P' signature
        if (Byte.toUnsignedInt(fileData[0]) == 0x89 && Byte.toUnsignedInt(fileData[1]) == 0x50)
            return PNG;
            // Finds the 'ÿØ' signature
        else if (Byte.toUnsignedInt(fileData[0]) == 0xFF && Byte.toUnsignedInt(fileData[1]) == 0xD8)
            return JPEG;
        return null;
    }

}
