package com.danielbukowski.photosharing.Validator;

import com.danielbukowski.photosharing.Enum.FileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.danielbukowski.photosharing.Enum.FileExtension.JPEG;
import static com.danielbukowski.photosharing.Enum.FileExtension.PNG;

public class ImageValidator implements ConstraintValidator<Image, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null) return false;

        return isFileTypeValid(file)
                && isFileExtensionValid(file)
                && isContentTypeValid(file);
    }

    private boolean isFileExtensionValid(MultipartFile multipartFile) {
        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        return JPEG.name().equalsIgnoreCase(fileExtension) || PNG.name().equalsIgnoreCase(fileExtension);
    }

    private boolean isContentTypeValid(MultipartFile multipartFile) {
        String fileContentType = multipartFile.getContentType();
        return Objects.equals(fileContentType, MediaType.IMAGE_JPEG_VALUE)
                || Objects.equals(fileContentType, MediaType.IMAGE_PNG_VALUE);
    }

    @SneakyThrows
    private boolean isFileTypeValid(MultipartFile multipartFile) {
        FileExtension fileType = getFileType(multipartFile.getBytes());
        return fileType.equals(PNG) || fileType.equals(JPEG);
    }

    // Checks Magic Bytes in a file
    private FileExtension getFileType(byte[] fileData) {
        // Checks signature ‰P
        if (Byte.toUnsignedInt(fileData[0]) == 0x89 && Byte.toUnsignedInt(fileData[1]) == 0x50)
            return PNG;
            // Checks signature ÿØ
        else if (Byte.toUnsignedInt(fileData[0]) == 0xFF && Byte.toUnsignedInt(fileData[1]) == 0xD8)
            return JPEG;
        throw new RuntimeException("Unknown file type extension");
    }
}
