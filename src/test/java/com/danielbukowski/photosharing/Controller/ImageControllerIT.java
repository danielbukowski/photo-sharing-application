package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Dto.NewCommentRequest;
import com.danielbukowski.photosharing.Dto.SimplePageResponse;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Enum.ExceptionMessageResponse;
import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Service.CommentService;
import com.danielbukowski.photosharing.Service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ImageController.class)
@WithMockUser
@ActiveProfiles("test")
class ImageControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ImageService imageService;
    @MockBean
    private CommentService commentService;
    @Mock
    private Account account;

    @Test
    void GetImageById_ImageExists_Returns200HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        given(imageService.getImageById(imageId, account))
                .willReturn(ImageDto.builder()
                        .contentType(MediaType.IMAGE_JPEG_VALUE)
                        .data(new byte[1])
                        .build());

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}", imageId)
                        .with(user(account))
                )
                //then
                .andExpect(status().isOk());
    }

    @Test
    void GetImageById_ImageDoesNotExist_Returns404HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        given(imageService.getImageById(imageId, account))
                .willThrow(new ImageNotFoundException(ExceptionMessageResponse.ACCOUNT_NOT_FOUND.getMessage()));

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}", imageId)
                        .with(user(account))
                )
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    void SaveCommentToImage_ImageIsPrivate_Returns403HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        var newCommentRequest = new NewCommentRequest("cool");
        given(commentService.saveCommentToImage(newCommentRequest, imageId, account))
                .willThrow(ImageNotFoundException.class);

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/comments", imageId)
                        .with(csrf())
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest))
                )
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    void SaveCommentToImage_ServiceSavesComment_Returns201HttpStatusCodeAndLocationHeader() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        var newCommentRequest = new NewCommentRequest("cool");
        given(commentService.saveCommentToImage(newCommentRequest, imageId, account))
                .willReturn(21L);

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/comments", imageId)
                        .with(csrf())
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest))
                )
                //then
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("location", "http://localhost/api/v1/images/%s/comments".formatted(imageId.toString())));
    }

    @Test
    void SaveCommentToImage_ContentInRequestBodyIsBlank_Returns400HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        var newCommentRequest = new NewCommentRequest("");

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/comments", imageId)
                        .with(csrf())
                        .with(user(account))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest))
                )
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(
                        "$.fieldNames.content[0]",
                        is("Should not be empty"))
                );
    }
    @Test
    void GetCommentsFromImage_ImageDoesNotExist_Returns404HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1,1);
        var pageNumber = Integer.valueOf(1);
        given(commentService.getCommentsFromImage(imageId, pageNumber, account))
                .willThrow(ImageNotFoundException.class);

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}/comments", imageId)
                        .with(user(account))
                        .param("pageNumber", String.valueOf(pageNumber))
                )
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    void GetCommentsFromImage_ServiceReturnsPage_Returns200HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1,1);
        var pageNumber = Integer.valueOf(1);
        given(commentService.getCommentsFromImage(imageId, pageNumber, account))
                .willReturn(
                        new SimplePageResponse<>(
                                1L,
                                List.of(),
                                pageNumber,
                                2,
                                false
                        )
                );

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}/comments", imageId)
                        .with(user(account))
                        .param("pageNumber", String.valueOf(pageNumber))
                )
                //then
                .andExpect(status().isOk());
    }

    @Test
    void GetListOfLatestImages_ServiceReturnsPage_Returns200HttpStatusCode() throws Exception {
        //given
        var pageNumber = Integer.valueOf(1);
        given(imageService.getIdsOfLatestImages(pageNumber))
                .willReturn(
                        new SimplePageResponse<>(
                                1L,
                                List.of(new UUID(2,2)),
                                pageNumber,
                                2,
                                false
                        )
                );

        //when
        mockMvc.perform(get("/api/v1/images")
                        .with(user(account))
                        .param("pageNumber", String.valueOf(pageNumber))
                )
                //then
                .andExpect(status().isOk());
    }

}