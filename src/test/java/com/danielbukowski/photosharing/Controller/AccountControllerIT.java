package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Config.SecurityConfigurationTest;
import com.danielbukowski.photosharing.Config.UserDetailsServiceTest;
import com.danielbukowski.photosharing.Dto.*;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Exception.InvalidTokenException;
import com.danielbukowski.photosharing.Service.AccountService;
import com.danielbukowski.photosharing.Service.EmailVerificationTokenService;
import com.danielbukowski.photosharing.Service.ImageService;
import com.danielbukowski.photosharing.Service.PasswordResetTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.anything;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = AccountController.class
)
@ActiveProfiles("test")
@Import({
        SecurityConfigurationTest.class,
        UserDetailsServiceTest.class
})
class AccountControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ImageService imageService;
    @MockBean
    private AccountService accountService;
    @MockBean
    private PasswordResetTokenService passwordResetTokenService;
    @MockBean
    private EmailVerificationTokenService emailVerificationTokenService;

    @Test
    void GetAccount_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(get("/api/v3/accounts"))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void GetAccount_UserIsEmailVerified_Returns200HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(get("/api/v3/accounts"))
                //then
                .andExpect(status().is(200));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void GetAccount_UserIsNotEmailVerified_Returns200HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(get("/api/v3/accounts"))
                //then
                .andExpect(status().is(200));
    }

    @Test
    void UpdateAccount_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(put("/api/v3/accounts"))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void UpdateAccount_UserIsEmailVerifiedAndBodyRequestIsEmpty_Returns400HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(put("/api/v3/accounts")
                )
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void UpdateAccount_UserIsNotEmailVerified_Returns403HttpStatusCode() throws Exception {
        //given
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest("dddd", "");

        //when
        mockMvc.perform(put("/api/v3/accounts")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountUpdateRequest))
                )
                //then
                .andExpect(status().is(403));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void UpdateAccount_NicknameIsRequestBodyPassesValidation_Throws204HttpStatusCode() throws Exception {
        //given
        AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest("c00lNickn33me", "");

        //when
        mockMvc.perform(put("/api/v3/accounts")
                        .content(objectMapper.writeValueAsString(accountUpdateRequest))
                        .contentType(APPLICATION_JSON))
                //then
                .andExpect(status().is(204));
    }

    @Test
    void CreateAccount_UserIsNotAuthenticated_Returns400HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(post("/api/v3/accounts"))
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void CreateAccount_RequestBodyIsEmpty_Returns400HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(post("/api/v3/accounts"))
                //then
                .andExpect(status().is(400))
                .andExpect(
                        jsonPath("$.reason",
                                Matchers.containsString("Required request body is missing"))
                );
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void CreateAccount_AccountRegisterRequestMatchesValidationRequirements_Returns201HttpStatus() throws Exception {
        //given
        var registerAccountRequest = new AccountRegisterRequest(
                "myemail@gmail.com",
                "nickname",
                "Pasw0rd?"
        );
        given(accountService.createAccount(registerAccountRequest))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(
                        post("/api/v3/accounts")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerAccountRequest))
                )
                //then
                .andExpect(status().is(201))
                .andExpect(header().stringValues(
                                "location", "http://localhost/api/v3/accounts/%s".formatted(new UUID(1, 1))
                        )
                );
    }

    @ParameterizedTest
    @WithUserDetails("userEmailVerified")
    @ValueSource(strings = {"email.com", "email@.com", "@gmail.com", "email@@gmail.com", "email@gmail."})
    void CreateAccount_EmailIsNotMatchingValidationRequirements_Returns400HttpStatusCode(String email) throws Exception {
        //when
        mockMvc.perform(
                        post("/api/v3/accounts")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                        "email":"%s"
                                        }
                                        """.formatted(email))
                )
                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(
                        jsonPath(
                                "$.fieldNames.email",
                                Matchers.contains("must be a well-formed email address")

                        ));
    }

    @ParameterizedTest
    @WithUserDetails("userEmailVerified")
    @ValueSource(strings = {"", "a", "aaaaaaa7", "aaaa!3A", "aaaaa!3", "aaaa!aA", "aaaaa3A"})
    void CreateAccount_PasswordIsNotMatchingTheRequirements_Returns400HttpStatusCode(String password) throws Exception {
        //when
        mockMvc.perform(
                        post("/api/v3/accounts")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                        "password":"%s"
                                        }
                                        """.formatted(password))
                )
                //then
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.fieldNames.password", anything())
                );
    }

    @Test
    void VerifyAccountByToken_UserIsNotAuthenticated_Returns204HttpStatusCode() throws Exception {
        //given
        mockMvc.perform(post("/api/v3/accounts/email-verification")
                        .param("token", new UUID(3, 3).toString()))
                //then
                .andExpect(status().is(204));
    }

    @Test
    void VerifyAccountByToken_TokenIsMissing_Returns400HttpStatusCode() throws Exception {
        //given
        mockMvc.perform(post("/api/v3/accounts/email-verification"))
                //then
                .andExpect(status().is(400))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));

    }

    @Test
    void ResendEmailVerificationToken_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(put("/api/v3/accounts/email-verification"))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void ResendEmailVerificationToken_AccountIsEmailVerified_Returns400HttpStatusCode() throws Exception {
        //given
        doThrow(new InvalidTokenException("An account has been already verified"))
                .when(emailVerificationTokenService).resendEmailVerificationToken(any());

        //when
        mockMvc.perform(put("/api/v3/accounts/email-verification"))
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void ResendEmailVerificationToken_AccountIsNotVerified_Returns204HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(put("/api/v3/accounts/email-verification"))
                //then
                .andExpect(status().is(204));
    }

    @Test
    void DeleteAccount_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(delete("/api/v3/accounts"))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void DeleteAccount_UserIsEmailVerified_Returns204HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(delete("/api/v3/accounts"))
                //then
                .andExpect(status().is(204));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void DeleteAccount_UserIsNotEmailVerified_Returns403HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(delete("/api/v3/accounts"))
                //then
                .andExpect(status().is(403));
    }

    @Test
    void ChangeAccountPassword_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(patch("/api/v3/accounts/password"))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void ChangeAccountPassword_UserIsEmailVerified_Returns400HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(patch("/api/v3/accounts/password"))
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void ChangeAccountPassword_UserNotIsEmailVerified_Returns403HttpStatusCode() throws Exception {
        //given
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest("0lDp@sswor3", "nN3wDp@sswor3");

        //when
        mockMvc.perform(patch("/api/v3/accounts/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequest)))
                //then
                .andExpect(status().is(403));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void ChangeAccountPassword_RequestBodyIsBlank_Returns400HttpStatusCode() throws Exception {
        //given
        var changePasswordRequest = new PasswordChangeRequest("", "");

        //when
        mockMvc.perform(patch("/api/v3/accounts/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                )
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void ChangeAccountPassword_NewPasswordInRequestBodyIsBlank_Returns400HttpStatusCode() throws Exception {
        //given
        var changePasswordRequest = new PasswordChangeRequest("myoldpass-wrd", "");

        //when
        mockMvc.perform(patch("/api/v3/accounts/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                )
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void ChangeAccountPassword_OldPasswordInRequestBodyIsBlank_Returns400HttpStatusCode() throws Exception {
        //given
        var changePasswordRequest = new PasswordChangeRequest("", "1@aaaaAAAAAAAA");

        //when
        mockMvc.perform(patch("/api/v3/accounts/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                )
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void ChangeAccountPassword_RequestBodyPassesValidation_Returns204HttpStatusCode() throws Exception {
        //given
        var changePasswordRequest = new PasswordChangeRequest("1@aaaaAAAAAAAA", "3@aaaaAAAAAAAA");

        //when
        mockMvc.perform(patch("/api/v3/accounts/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                )
                //then
                .andExpect(status().is(204));
    }

    @Test
    void GetImagesFromAccount_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(get("/api/v3/accounts/images"))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void GetImagesFromAccount_UserIsNotEmailVerified_Returns200HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(get("/api/v3/accounts/images"))
                //then
                .andExpect(status().is(200));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void GetImagesFromAccount_UserIsEmailVerified_Returns200HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(get("/api/v3/accounts/images"))
                //then
                .andExpect(status().is(200));
    }

    @Test
    void SaveImageToAccount_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                IMAGE_JPEG_VALUE,
                new byte[]{-1, -40}
        );
        ImagePropertiesRequest imageProperties = new ImagePropertiesRequest(
                true,
                "image"
        );
        MockMultipartFile jsonImageProperties = new MockMultipartFile("imageProperties",
                "imageProperties",
                "application/json",
                objectMapper.writeValueAsBytes(imageProperties));

        given(imageService.saveImageToAccount(eq(image), any(Account.class), eq(imageProperties)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v3/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                )
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void SaveImageToAccount_UserIsNotEmailVerified_Returns403HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                IMAGE_JPEG_VALUE,
                new byte[]{-1, -40}
        );
        ImagePropertiesRequest imageProperties = new ImagePropertiesRequest(
                true,
                "image"
        );
        MockMultipartFile jsonImageProperties = new MockMultipartFile("imageProperties",
                "imageProperties",
                "application/json",
                objectMapper.writeValueAsBytes(imageProperties));

        given(imageService.saveImageToAccount(eq(image), any(Account.class), eq(imageProperties)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v3/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                )
                //then
                .andExpect(status().is(403));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void SaveImageToAccount_FileTypeIsNeitherJpegNorPng_Returns400HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                MULTIPART_FORM_DATA_VALUE,
                new byte[]{}
        );
        ImagePropertiesRequest imageProperties = new ImagePropertiesRequest(
                true,
                "image"
        );
        MockMultipartFile jsonImageProperties = new MockMultipartFile("imageProperties",
                "imageProperties",
                "application/json",
                objectMapper.writeValueAsBytes(imageProperties));

        given(imageService.saveImageToAccount(
                eq(image),
                any(Account.class),
                eq(imageProperties))
        ).willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v3/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                )
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void SaveImageToAccount_FileExtensionIsNeitherJpegNorPng_Returns400HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.gif",
                IMAGE_JPEG_VALUE,
                new byte[]{-1, -40}
        );
        ImagePropertiesRequest imageProperties = new ImagePropertiesRequest(
                true,
                "image"
        );
        MockMultipartFile jsonImageProperties = new MockMultipartFile("imageProperties",
                "imageProperties",
                "application/json",
                objectMapper.writeValueAsBytes(imageProperties));

        given(imageService.saveImageToAccount(
                eq(image),
                any(Account.class),
                eq(imageProperties)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v3/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                )
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void SaveImageToAccount_ImageInRequestBodyIsNull_Returns400HttpStatusCode() throws Exception {
        //given
        given(imageService.saveImageToAccount(any(), any(Account.class), any()))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v3/accounts/images")
                )
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void SaveImageToAccount_ContentTypeIsNeitherJpegNorPng_Returns400HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                MULTIPART_FORM_DATA_VALUE,
                new byte[]{-1, -40}
        );
        ImagePropertiesRequest imageProperties = new ImagePropertiesRequest(
                true,
                "image"
        );
        MockMultipartFile jsonImageProperties = new MockMultipartFile("imageProperties",
                "imageProperties",
                "application/json",
                objectMapper.writeValueAsBytes(imageProperties));
        given(imageService.saveImageToAccount(eq(image), any(Account.class), eq(imageProperties)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v3/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                )
                //then
                .andExpect(status().is(400));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void SaveImageToAccount_JpegImageIsSent_Returns201HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                IMAGE_JPEG_VALUE,
                new byte[]{-1, -40}
        );
        ImagePropertiesRequest imageProperties = new ImagePropertiesRequest(
                true,
                "image"
        );
        MockMultipartFile jsonImageProperties = new MockMultipartFile("imageProperties",
                "imageProperties",
                "application/json",
                objectMapper.writeValueAsBytes(imageProperties));

        given(imageService.saveImageToAccount(eq(image), any(Account.class), eq(imageProperties)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v3/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                )
                //then
                .andExpect(status().is(201));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void SaveImageToAccount_PngImageIsSent_Returns201HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.png",
                IMAGE_PNG_VALUE,
                new byte[]{-119, 80}
        );
        ImagePropertiesRequest imageProperties = new ImagePropertiesRequest(
                true,
                "image"
        );
        MockMultipartFile jsonImageProperties = new MockMultipartFile("imageProperties",
                "imageProperties",
                "application/json",
                objectMapper.writeValueAsBytes(imageProperties));

        given(imageService.saveImageToAccount(eq(image), any(Account.class), eq(imageProperties)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v3/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                )
                //then
                .andExpect(status().is(201));
    }


    @Test
    void DeleteImageFromAccount_UserIsNotAuthenticated_Returns401HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);

        //when
        mockMvc.perform(delete("/api/v3/accounts/images/{imageId}", imageId))
                //then
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("userNotEmailVerified")
    void DeleteImageFromAccount_UserIsNotEmailVerified_Returns403HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);

        //when
        mockMvc.perform(delete("/api/v3/accounts/images/{imageId}", imageId))
                //then
                .andExpect(status().is(403));
    }

    @Test
    @WithUserDetails("userEmailVerified")
    void DeleteImageFromAccount_UserIsEmailVerified_Returns204HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);

        //when
        mockMvc.perform(delete("/api/v3/accounts/images/{imageId}", imageId)
                )
                //then
                .andExpect(status().is(204));
    }

    @Test
    void CreateResetPasswordToken_UserIsNotAuthenticated_Returns204HttpStatusCode() throws Exception {
        //given
        var passwordResetRequest = new PasswordResetRequest("email@gmail.com");

        //when
        mockMvc.perform(post("/api/v3/accounts/password-reset")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetRequest))
                )
                //then
                .andExpect(status().is(204));
    }

    @Test
    void CreateResetPasswordToken_RequestBodyIsBlank_Returns400HttpStatusCode() throws Exception {
        //given
        var passwordResetRequest = new PasswordResetRequest("");

        //when
        mockMvc.perform(post("/api/v3/accounts/password-reset")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetRequest))
                )
                //then
                .andExpect(status().is(400));
    }

    @Test
    void ChangeResetPasswordByPasswordResetTokenId_UserIsNotAuthenticated_Returns204HttpStatusCode() throws Exception {
        //given
        var passwordChangeRequest = new PasswordChangeRequest("myOldpas0!d", "myn3Wpas0!d");

        //when
        mockMvc.perform(put("/api/v3/accounts/password-reset")
                        .contentType(APPLICATION_JSON)
                        .param("token", new UUID(1, 1).toString())
                        .content(objectMapper.writeValueAsString(passwordChangeRequest))
                )
                //then
                .andExpect(status().is(204));
    }

    @Test
    void ChangeResetPasswordByPasswordResetTokenId_RequestParameterAndRequestBodyAreNull_Returns400HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(put("/api/v3/accounts/password-reset"))
                //then
                .andExpect(status().is(400));
    }

    @Test
    void ChangeResetPasswordByPasswordResetTokenId_RequestBodyIsNull_Returns400HttpStatusCode() throws Exception {
        //given
        var token = new UUID(2, 2).toString();

        //when
        mockMvc.perform(put("/api/v3/accounts/password-reset")
                        .param("token", token))
                //then
                .andExpect(status().is(400));
    }

}