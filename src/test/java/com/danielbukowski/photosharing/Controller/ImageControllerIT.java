package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Config.SecurityConfigurationTest;
import com.danielbukowski.photosharing.Config.UserDetailsServiceTest;
import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Dto.NewCommentRequest;
import com.danielbukowski.photosharing.Dto.SimplePageResponse;
import com.danielbukowski.photosharing.Enum.ExceptionMessageResponse;
import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Service.CommentService;
import com.danielbukowski.photosharing.Service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ImageController.class
)
@ActiveProfiles("test")
@Import({
        SecurityConfigurationTest.class,
        UserDetailsServiceTest.class
})
class ImageControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ImageService imageService;
    @MockBean
    private CommentService commentService;

    @Test
    void GetImageById_UserIsNotAuthenticated_Returns200HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        given(imageService.getImageById(eq(imageId), any()))
                .willReturn(ImageDto.builder()
                        .contentType(IMAGE_PNG_VALUE)
                        .data(new byte[1])
                        .build()
                );

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}", imageId)
                )
                //then
                .andExpect(status().is(200));
    }

    @Test
    void GetImageById_ImageDoesNotExist_Returns404HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        given(imageService.getImageById(eq(imageId), any()))
                .willThrow(new ImageNotFoundException(ExceptionMessageResponse.ACCOUNT_NOT_FOUND.getMessage()));

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}", imageId))
                //then
                .andExpect(status().is(404));
    }

    @Test
    void SaveCommentToImage_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/comments", imageId))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void SaveCommentToImage_UserIsNotEmailVerified_Returns403HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        var newCommentRequest = new NewCommentRequest("cool");

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/comments", imageId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest))
                )
                //then
                .andExpect(status().is(403));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void SaveCommentToImage_ImageIsPrivate_Returns404HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        var newCommentRequest = new NewCommentRequest("cool");
        given(commentService.saveCommentToImage(eq(newCommentRequest), eq(imageId), any()))
                .willThrow(ImageNotFoundException.class);

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/comments", imageId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest))
                )
                //then
                .andExpect(status().is(404));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void SaveCommentToImage_SavesComment_Returns201HttpStatusCodeAndLocationHeader() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        var newCommentRequest = new NewCommentRequest("cool");
        given(commentService.saveCommentToImage(eq(newCommentRequest), eq(imageId), any()))
                .willReturn(21L);

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/comments", imageId)

                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest))
                )
                //then
                .andExpect(status().is(201))
                .andExpect(header().stringValues("location", "http://localhost/api/v1/images/%s/comments".formatted(imageId.toString())));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void SaveCommentToImage_ContentInRequestBodyIsBlank_Returns400HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        var newCommentRequest = new NewCommentRequest("");

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/comments", imageId)

                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequest))
                )
                //then
                .andExpect(status().is(400))
                .andExpect(jsonPath(
                        "$.fieldNames.content[0]",
                        is("Should not be blank"))
                );
    }

    @Test
    void GetCommentsFromImage_UserIsNotAuthenticated_Returns200HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1,1);

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}/comments", imageId)
                )
                //then
                .andExpect(status().is(200));
    }

    @Test
    void GetCommentsFromImage_PageNumberParameterIsMissing_Returns200HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1,1);
        var pageNumber = Integer.valueOf(0);
        given(commentService.getCommentsFromImage(eq(imageId), eq(pageNumber), any()))
                .willReturn(
                        new SimplePageResponse<>(
                                1L,
                                List.of(),
                                pageNumber,
                                2,
                                true
                        )
                );

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}/comments", imageId))
                //then
                .andExpect(status().is(200));
    }

    @Test
    void GetCommentsFromImage_ImageDoesNotExist_Returns404HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1,1);
        var pageNumber = Integer.valueOf(1);
        given(commentService.getCommentsFromImage(eq(imageId), eq(pageNumber), any()))
                .willThrow(ImageNotFoundException.class);

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}/comments", imageId)
                        .param("pageNumber", String.valueOf(pageNumber))
                )
                //then
                .andExpect(status().is(404));
    }

    @Test
    void GetLatestImages_UserIsNotAuthenticated_Returns200HttpStatusCode() throws Exception {
        //given
        var pageNumber = Integer.valueOf(1);
        given(imageService.getIdsOfLatestImages(pageNumber))
                .willReturn(
                        new SimplePageResponse<>(
                                1L,
                                List.of(new UUID(2,2)),
                                pageNumber,
                                2,
                                true
                        )
                );

        //when
        mockMvc.perform(get("/api/v1/images")
                        .param("pageNumber", String.valueOf(pageNumber))
                )
                //then
                .andExpect(status().is(200));
    }

    @Test
    void GetLatestImages_PageNumberParameterIsMissing_Returns200HttpStatusCode() throws Exception {
        //given
        var pageNumber = Integer.valueOf(0);
        given(imageService.getIdsOfLatestImages(pageNumber))
                .willReturn(
                        new SimplePageResponse<>(
                                1L,
                                List.of(new UUID(2,2)),
                                pageNumber,
                                2,
                                true
                        )
                );

        //when
        mockMvc.perform(get("/api/v1/images"))
                //then
                .andExpect(status().is(200));
    }

    @Test
    void AddLikeToImage_AccountIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(4,4);

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/likes", imageId))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void AddLikeToImage_AccountIsNotEmailVerified_Returns403HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(4,4);

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/likes", imageId))
                //then
                .andExpect(status().is(403));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void AddLikeToImage_AccountIsEmailVerified_Returns201HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(4,4);

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/likes", imageId))
                //then
                .andExpect(status().is(201));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void AddLikeToImage_ImageIdIsString_Returns400HttpStatusCode() throws Exception {
        //given
        var imageId = "I like uuids";

        //when
        mockMvc.perform(post("/api/v1/images/{imageId}/likes", imageId))
                //then
                .andExpect(status().is(400));
    }

    @Test
    void getNumberOfLikesFromImage_AccountIsNotAuthenticated_Returns200HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(4,4);

        //when
        mockMvc.perform(get("/api/v1/images/{imageId}/likes", imageId))
                //then
                .andExpect(status().is(200));
    }

    @Test
    void getNumberOfLikesFromImage_ImageIdIsMissing_Returns400HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(get("/api/v1/images/{imageId}/likes", ""))
                //then
                .andExpect(status().is(400));
    }

    @Test
    void RemoveLikeFromImage_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(4,4);

        //when
        mockMvc.perform(delete("/api/v1/images/{imageId}/likes", imageId))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void RemoveLikeFromImage_UserIsNotEmailVerified_Returns403HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(4,4);

        //when
        mockMvc.perform(delete("/api/v1/images/{imageId}/likes", imageId))
                //then
                .andExpect(status().is(403));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void RemoveLikeFromImage_RemovesLike_Returns204HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(4,4);

        //when
        mockMvc.perform(delete("/api/v1/images/{imageId}/likes", imageId))
                //then
                .andExpect(status().is(204));
    }

}