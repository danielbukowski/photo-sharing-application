package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Dto.ImagePropertiesRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Image;
import com.danielbukowski.photosharing.Enum.ExceptionMessageResponse;
import com.danielbukowski.photosharing.Exception.ImageException;
import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Mapper.ImageMapper;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import com.danielbukowski.photosharing.Util.EncryptionUtils;
import com.danielbukowski.photosharing.Util.ImageUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;
    @Mock
    private Clock clock;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private ImageMapper imageMapper;
    @Mock
    private S3Service s3Service;
    @Mock
    private ImageUtils imageUtils;
    @Mock
    private EncryptionUtils encryptionUtils;

    private final ZonedDateTime now = ZonedDateTime.of(
            2023,
            6,
            7,
            21,
            37,
            0,
            0,
            ZoneId.of("GMT")
    );

    @Test
    void GetImageById_ImageDoesNotExist_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var account = Account.builder().build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.empty());

        //when
        var actualException = assertThrows(
                ImageNotFoundException.class,
                () -> imageService.getImageById(imageId, account)
        );
        //then
        assertEquals(ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(), actualException.getMessage());
    }

    @Test
    void GetImageById_AccountDoesNotHaveAccessToImage_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var image = Image.builder()
                .isPrivate(true)
                .build();
        var account = Account.builder()
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(imageUtils.hasAccessToImage(account, image))
                .willReturn(false);

        //when
        var actualException = assertThrows(ImageNotFoundException.class,
                () -> imageService.getImageById(imageId, account)
        );

        //then
        assertEquals(ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(), actualException.getMessage());
    }

    @Test
    void GetImageById_AccountDoesHaveAccessToImage_ReturnsImageDto() {
        //given
        var imageId = new UUID(1, 1);
        var account = Account.builder()
                .id(new UUID(2, 2))
                .build();
        var image = Image.builder()
                .id(new UUID(1, 1))
                .account(account)
                .build();
        var data = new byte[1];
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));

        given(s3Service.getImageFromS3(new UUID(2, 2),
                new UUID(1, 1)))
                .willReturn(data);
        given(encryptionUtils.decrypt(data))
                .willReturn(data);
        given(imageUtils.decompressImage(any()))
                .willReturn(data);
        given(imageMapper.fromImageToImageDto(data, image))
                .willReturn(ImageDto.builder().build());
        given(imageUtils.hasAccessToImage(account, image))
                .willReturn(true);

        //when
        var actualImageDto = imageService.getImageById(imageId, account);

        //then
        assertNotNull(actualImageDto);
    }

    @Test
    void SaveImageToAccount_SavesImageToAccount_ReturnsId() {
        //given
        var data = new byte[1];
        var image = new MockMultipartFile(
                "image",
                data
        );
        given(clock.getZone())
                .willReturn(now.getZone());
        given(clock.instant())
                .willReturn(now.toInstant());
        var imageProperties = new ImagePropertiesRequest(true, "myImage");
        var account = Account.builder()
                .id(new UUID(1, 1))
                .build();
        var savedImageWithId = Image.builder()
                .id(new UUID(2, 2))
                .build();
        given(imageRepository.save(any()))
                .willReturn(savedImageWithId);

        given(encryptionUtils.encrypt(any()))
                .willReturn(data);
        given(imageUtils.compressImage(any()))
                .willReturn(data);

        //when
        var actualId = imageService.saveImageToAccount(
                image, account, imageProperties
        );

        //then
        assertEquals(
                new UUID(2, 2), actualId
        );
    }

    @Test
    void DeleteImageFromAccount_ImageExists_DeletesImage() {
        ///given
        var accountId = new UUID(1, 1);
        var imageId = new UUID(2, 2);

        //when
        Assertions.assertDoesNotThrow(
                () -> imageService.deleteImageFromAccount(accountId, imageId)
        );

        //then
        verify(s3Service, times(1))
                .deleteImageFromS3(accountId, imageId);
    }

    @Test
    void GetIdsOfLatestImages_FindsSomeImages_ReturnsSimplePageResponse() {
        //given
        var pageNumber = Integer.valueOf(1);
        given(imageRepository.findAll(any(PageRequest.class)))
                .willReturn(
                        new PageImpl<>(List.of(
                                Image.builder()
                                        .id(new UUID(3, 3))
                                        .build()
                        ))
                );

        //when
        var actualSimplePageResponse = imageService.getIdsOfLatestImages(
                pageNumber
        );

        //then
        assertNotNull(actualSimplePageResponse);
    }

    @Test
    void AddLikeToImage_ImageDoesNotExist_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var account = Account.builder().build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.empty());

        //when
        var actualException = Assertions.assertThrows(
                ImageNotFoundException.class,
                () -> imageService.addLikeToImage(imageId, account)
        );

        //then
        assertEquals(ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(),
                actualException.getMessage());
    }

    @Test
    void AddLikeToImage_AccountDoesNotHaveAccessToImage_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var image = Image.builder()
                .id(imageId)
                .build();
        var account = Account.builder().build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(imageUtils.hasAccessToImage(account, image))
                .willReturn(false);

        //when
        var actualException = Assertions.assertThrows(
                ImageNotFoundException.class,
                () -> imageService.addLikeToImage(imageId, account)
        );

        //then
        assertEquals(ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(),
                actualException.getMessage());
    }

    @Test
    void AddLikeToImage_AccountAlreadyLikedImage_ThrowsImageException() {
        //given
        var imageId = new UUID(1, 1);
        var account = Account.builder()
                .id(new UUID(2, 2))
                .build();
        var image = Image.builder()
                .id(imageId)
                .likes(Set.of(account))
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(imageUtils.hasAccessToImage(account, image))
                .willReturn(true);

        //when
        var actualException = Assertions.assertThrows(
                ImageException.class,
                () -> imageService.addLikeToImage(imageId, account)
        );

        //then
        assertEquals("You have already liked this image",
                actualException.getMessage());
    }

    @Test
    void AddLikeToImage_AccountDidNotLikeImage_ServiceAddsLikeToImage() {
        //given
        var imageId = new UUID(1, 1);
        var account = Account.builder()
                .id(new UUID(2, 2))
                .build();
        var image = Image.builder()
                .id(imageId)
                .likes(new HashSet<>())
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(imageUtils.hasAccessToImage(account, image))
                .willReturn(true);

        //when
        imageService.addLikeToImage(imageId, account);

        //then
        assertEquals(1, image.getLikes().size());
    }

    @Test
    void GetNumberOfLikesFromImage_ImageDoesNotExist_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var account = Account.builder().build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.empty());

        //when
        var actualException = Assertions.assertThrows(
                ImageNotFoundException.class,
                () -> imageService.getNumberOfLikesFromImage(imageId, account)
        );

        //then
        assertEquals(ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(),
                actualException.getMessage());
    }

    @Test
    void GetNumberOfLikesFromImage_DoesNotHaveAccessToImage_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var image = Image.builder()
                .id(imageId)
                .build();
        var account = Account.builder().build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));

        //when
        var actualException = Assertions.assertThrows(
                ImageNotFoundException.class,
                () -> imageService.getNumberOfLikesFromImage(imageId, account)
        );

        //then
        assertEquals(ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(),
                actualException.getMessage());
    }

    @Test
    void GetNumberOfLikesFromImage_FindsAccessibleImage_ReturnsNumberOfLikes() {
        //given
        var account = Account.builder().build();
        Set<Account> likes = new HashSet<>();
        likes.add(account);
        var imageId = new UUID(1, 1);
        var image = Image.builder()
                .id(imageId)
                .likes(likes)
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(imageUtils.hasAccessToImage(account, image))
                .willReturn(true);

        //when
        var actualNumberOfLikes = imageService.getNumberOfLikesFromImage(imageId, account);

        //then
        assertEquals(1, actualNumberOfLikes);
    }

    @Test
    void RemoveLikeFromImage_ImageDoesNotExist_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var account = Account.builder().build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.empty());

        //when
        var actualException = Assertions.assertThrows(
                ImageNotFoundException.class,
                () -> imageService.removeLikeFromImage(imageId, account)
        );

        //then
        assertEquals(ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(),
                actualException.getMessage());
    }

    @Test
    void RemoveLikeFromImage_DoesNotHaveAccessToImage_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var image = Image.builder()
                .id(imageId)
                .build();
        var account = Account.builder().build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(imageUtils.hasAccessToImage(account, image))
                .willReturn(false);

        //when
        var actualException = Assertions.assertThrows(
                ImageNotFoundException.class,
                () -> imageService.removeLikeFromImage(imageId, account)
        );

        //then
        assertEquals(ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(),
                actualException.getMessage());
    }

    @Test
    void RemoveLikeFromImage_FindsAccessibleImage_RemovesLikeFromImage() {
        //given
        var account = Account.builder()
                .id(new UUID(4,4))
                .build();
        Set<Account> likes = new HashSet<>();
        likes.add(account);
        likes.add(Account.builder()
                .id(new UUID(5,5))
                .build()
        );
        var imageId = new UUID(1, 1);
        var image = Image.builder()
                .id(imageId)
                .likes(likes)
                .build();

        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(imageUtils.hasAccessToImage(account, image))
                .willReturn(true);

        //when
        imageService.removeLikeFromImage(imageId, account);

        //then
        assertEquals(1, image.getLikes().size());
    }

}