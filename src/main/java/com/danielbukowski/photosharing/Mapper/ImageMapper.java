package com.danielbukowski.photosharing.Mapper;

import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Entity.Image;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageDto fromImageToImageDto(byte[] data, Image image) {
        return ImageDto.builder()
                .data(data)
                .contentType(image.getContentType())
                .build();
    }

}
