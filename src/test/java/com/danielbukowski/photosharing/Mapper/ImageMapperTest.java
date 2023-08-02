package com.danielbukowski.photosharing.Mapper;

import com.danielbukowski.photosharing.Entity.Image;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageMapperTest {

    private final ImageMapper imageMapper = new ImageMapper();

    @Test
    void FromImageToImageDto_MappedFieldsAreTheSame_ReturnsEqualObject() {
        // given
        byte[] data = new byte[]{};
        Image image = Image.builder()
                .createDate(LocalDateTime.now())
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .build();

        //when
        var actualImageDto = imageMapper.fromImageToImageDto(data, image);

        //then
        assertEquals(data, actualImageDto.data());
        assertEquals(image.getContentType(), actualImageDto.contentType());
    }

}