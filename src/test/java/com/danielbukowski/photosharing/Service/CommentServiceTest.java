package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.NewCommentRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Comment;
import com.danielbukowski.photosharing.Entity.Image;
import com.danielbukowski.photosharing.Enum.ExceptionMessageResponse;
import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Mapper.CommentMapper;
import com.danielbukowski.photosharing.Repository.CommentRepository;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private CommentMapper commentMapper;

    @Test
    void SaveCommentToImage_ImageDoesNotExist_ThrowsImageNotFoundException(){
        //given
        var newCommentRequest = new NewCommentRequest("huh");
        var imageId = new UUID(1,1);
        var account = new Account();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.empty());

        //when
        var actualException = assertThrows(
                ImageNotFoundException.class,
                () -> commentService.saveCommentToImage(
                        newCommentRequest, imageId, account
                )
        );

        //then
        assertEquals(
                ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(), actualException.getMessage()
        );
    }

    @Test
    void SaveCommentToImage_ImageIsPrivateAndDoesNotBelongToAccount_ThrowsImageNotFoundException(){
        //given
        var newCommentRequest = new NewCommentRequest("huh");
        var imageId = new UUID(1,1);
        var account = Account.builder()
                .id(new UUID(5,5))
                .build();
        var image = Image.builder()
                .id(imageId)
                .isPrivate(true)
                .account(Account.builder()
                        .id(new UUID(2,2))
                        .build()
                )
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));

        //when
        var actualException = assertThrows(
                ImageNotFoundException.class,
                () -> commentService.saveCommentToImage(
                        newCommentRequest, imageId, account
                )
        );

        //then
        assertEquals(
                ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(), actualException.getMessage()
        );
    }

    @Test
    void SaveCommentToImage_ImageIsPrivateAndBelongsToAccount_ReturnsId(){
        //given
        var newCommentRequest = new NewCommentRequest("huh");
        var imageId = new UUID(1,1);
        var account = Account.builder()
                .id(new UUID(2,2))
                .build();
        var image = Image.builder()
                .isPrivate(true)
                .id(imageId)
                .account(Account.builder()
                        .id(new UUID(2,2))
                        .build()
                )
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(commentRepository.save(any()))
                .willReturn(Comment.builder()
                        .id(1L)
                        .build()
                );

        //when
        var actualId = commentService.saveCommentToImage(
                newCommentRequest, imageId, account
        );

        //then
        assertEquals(
                1L, actualId
        );
    }

    @Test
    void SaveCommentToImage_ImageIsNotPrivateAndDoesNotBelongToAccount_ReturnsId() {
        //given
        var newCommentRequest = new NewCommentRequest("huh");
        var imageId = new UUID(1,1);
        var account = Account.builder()
                .id(new UUID(2,2))
                .build();
        var image = Image.builder()
                .isPrivate(false)
                .id(imageId)
                .account(Account.builder()
                        .id(new UUID(4,4))
                        .build()
                )
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(commentRepository.save(any()))
                .willReturn(Comment.builder()
                        .id(1L)
                        .build()
                );

        //when
        var actualId = commentService.saveCommentToImage(
                newCommentRequest, imageId, account
        );

        //then
        assertEquals(
                1L, actualId
        );
    }

    @Test
    void SaveCommentToImage_ImageIsNotPrivateAndBelongsToAccount_ReturnsId() {
        //given
        var newCommentRequest = new NewCommentRequest("huh");
        var imageId = new UUID(1,1);
        var account = Account.builder()
                .id(new UUID(2,2))
                .build();
        var image = Image.builder()
                .isPrivate(false)
                .id(imageId)
                .account(Account.builder()
                        .id(new UUID(2,2))
                        .build()
                )
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(image));
        given(commentRepository.save(any()))
                .willReturn(Comment.builder()
                        .id(1L)
                        .build()
                );

        //when
        var actualId = commentService.saveCommentToImage(
                newCommentRequest, imageId, account
        );

        //then
        assertEquals(
                1L, actualId
        );
    }

    @Test
    void GetCommentsFromImage_ImageDoesNotExist_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var pageNumber = Integer.valueOf(1);
        var account = Account.builder().build();

        //when
        var actualException = assertThrows(ImageNotFoundException.class,
                () -> commentService.getCommentsFromImage(
                        imageId, pageNumber, account
                ));

        //then
        assertEquals(
                ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(), actualException.getMessage()
        );
    }

    @Test
    void GetCommentsFromImage_ImageDoesNotBelongToAccount_ThrowsImageNotFoundException() {
        //given
        var imageId = new UUID(1, 1);
        var pageNumber = Integer.valueOf(1);
        var account = Account.builder()
                .id(new UUID(4,4))
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(Image.builder()
                        .account(Account.builder()
                                .id(new UUID(2, 2))
                                .build()
                        )
                        .build())
                );

        //when
        var actualException = assertThrows(ImageNotFoundException.class,
                () -> commentService.getCommentsFromImage(
                        imageId, pageNumber, account
                ));

        //then
        assertEquals(
                ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(), actualException.getMessage()
        );
    }

    @Test
    void GetCommentsFromImage_ImageExistsAndBelongsToAccount_ReturnsSimplePageResponse() {
        //given
        var imageId = new UUID(1, 1);
        var pageNumber = Integer.valueOf(1);
        var account = Account.builder()
                .id(new UUID(4,4))
                .build();
        given(imageRepository.findById(imageId))
                .willReturn(Optional.of(Image.builder()
                        .account(Account.builder()
                                .id(new UUID(4, 4))
                                .build()
                        )
                        .build())
                );
        given(commentRepository.getByImageId(PageRequest.of(pageNumber, 20), imageId))
                .willReturn(new PageImpl<>(
                        List.of(Comment.builder()
                                .content("huh")
                                .build())
                ));
        //when

        var expectedResult = commentService.getCommentsFromImage(
                imageId, pageNumber, account
        );

        //then
        assertNotNull(expectedResult);
    }

}