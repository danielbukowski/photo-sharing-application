package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.ChangePasswordRequest;
import com.danielbukowski.photosharing.Dto.ImagePropertiesRequest;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Exception.AccountAlreadyExistsException;
import com.danielbukowski.photosharing.Exception.BadVerificationTokenException;
import com.danielbukowski.photosharing.Exception.InvalidPasswordException;
import com.danielbukowski.photosharing.Service.AccountService;
import com.danielbukowski.photosharing.Service.EmailVerificationTokenService;
import com.danielbukowski.photosharing.Service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.danielbukowski.photosharing.Enum.ExceptionMessageResponse.PASSWORD_SHOULD_NOT_BE_THE_SAME;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = AccountController.class)
@WithMockUser
@ActiveProfiles("test")
class AccountControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private EmailVerificationTokenService emailVerificationTokenService;
    @Mock
    private Account account;

    @Test
    void CreateAccount_RequestBodyIsEmpty_Returns400HttpStatusCode() throws Exception {
        //given
        AccountRegisterRequest accountRegisterRequest = new AccountRegisterRequest("", "");

        //when
        mockMvc.perform(
                        post("/api/v2/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(accountRegisterRequest))
                                .with(csrf())
                )
                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath(
                                "$.fieldNames.password",
                                hasSize(6))
                )
                .andExpectAll(
                        jsonPath(
                                "$.fieldNames.password").value(
                                containsInAnyOrder(
                                        "Should not be empty",
                                        "Should have one lowercase letter",
                                        "Should have one uppercase letter",
                                        "Should have one special character",
                                        "Should have one digit from 1 to 9",
                                        "Should must be 8-32 characters long"
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.fieldNames.email"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$.fieldNames.email[0]",
                                is("Should not be empty"))
                );
    }

    @Test
    void CreateAccount_ServiceThrowsAccountAlreadyExistsException_Returns400HttpStatusCode() throws Exception {
        //given
        var registerAccountRequest = new AccountRegisterRequest(
                "myemail@gmail.com",
                "Pasw0rd?"
        );
        given(accountService.createAccount(registerAccountRequest))
                .willThrow(AccountAlreadyExistsException.class);

        //when
        mockMvc.perform(
                        post("/api/v2/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerAccountRequest))
                                .with(csrf()))
                //then
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void CreateAccount_AccountRegisterRequestMatchesAllRequirements_Returns201HttpStatus() throws Exception {
        //given
        var registerAccountRequest = new AccountRegisterRequest(
                "myemail@gmail.com",
                "Pasw0rd?"
        );
        given(accountService.createAccount(registerAccountRequest))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(
                        post("/api/v2/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerAccountRequest))
                                .with(csrf())
                )
                //then
                .andExpect(status().isCreated())
                .andExpect(header().stringValues(
                                "location", "http://localhost/api/v2/accounts/%s".formatted(new UUID(1, 1))
                        )
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {"email.com", "email@.com", "@gmail.com", "email@@gmail.com", "email@gmail."})
    void CreateAccount_EmailIsNotMatchingTheRequirements_ReturnsErrorMessageAboutNotWellFormedEmail(String email) throws Exception {
        //when
        mockMvc.perform(
                        post("/api/v2/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "email":"%s"
                                        }
                                        """.formatted(email))
                                .with(csrf())
                )
                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath(
                                "$.fieldNames.email",
                                contains("must be a well-formed email address")

                        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "aaaaaaa7", "aaaa!3A", "aaaaa!3", "aaaa!aA", "aaaaa3A"})
    void CreateAccount_PasswordIsNotMatchingTheRequirements_ReturnsMessageAboutWrongPassword(String password) throws Exception {
        //when
        mockMvc.perform(
                        post("/api/v2/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "password":"%s"
                                        }
                                        """.formatted(password))
                                .with(csrf())
                )
                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        jsonPath(
                                "$.fieldNames.password",
                                anything())
                );
    }

    @Test
    void VerifyAccountByToken_ServiceThrowsNoException_Returns204HttpStatusCode() throws Exception {
        //given
        var token = new UUID(0, 0);

        //when
        mockMvc.perform(post("/api/v2/accounts/email-verification")
                        .with(csrf())
                        .param("token", token.toString())
                        .with(user(account)
                        ))
                //then
                .andExpect(status().isNoContent());
    }

    @Test
    void VerifyAccountByToken_ServiceThrowsException_Returns400HttpStatusCode() throws Exception {
        //given
        var token = new UUID(0, 0);
        doThrow(new BadVerificationTokenException("This token has already expired"))
                .when(emailVerificationTokenService).verifyEmailVerificationToken(token);

        //when
        mockMvc.perform(post("/api/v2/accounts/email-verification")
                        .with(csrf())
                        .param("token", token.toString())
                        .with(user(account)
                        ))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void ResendEmailVerificationToken_AccountIsAlreadyVerified_Returns400HttpStatusCode() throws Exception {
        //given
        doThrow(new BadVerificationTokenException("An account has been already verified"))
                .when(emailVerificationTokenService).resendEmailVerificationToken(account);

        //when
        mockMvc.perform(put("/api/v2/accounts/email-verification")
                        .with(csrf())

                        .with(user(account)
                        ))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void ResendEmailVerificationToken_AccountIsNotVerified_Returns204HttpStatusCode() throws Exception {
        //when
        mockMvc.perform(put("/api/v2/accounts/email-verification")
                        .with(csrf())
                        .with(user(account)
                        ))
                //then
                .andExpect(status().isNoContent());
    }

    @Test
    void DeleteAccount_SendsRequest_Returns204HttpStatusCode() throws Exception {
        //given
        given(account.getId())
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(
                        delete("/api/v2/accounts")
                                .with(csrf())
                                .with(user(account))
                )
                //then
                .andExpect(status().isNoContent());
    }

    @Test
    void ChangeAccountPassword_ThrowsNoException_Returns204HttpStatusCode() throws Exception {
        //given
        var changePasswordRequest = new ChangePasswordRequest("P@ssw0rd");

        //when
        mockMvc.perform(patch("/api/v2/accounts/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                )
                //then
                .andExpect(status().isNoContent());
    }

    @Test
    void ChangeAccountPassword_ServiceThrowsInvalidPasswordException_Returns400HttpStatusCode() throws Exception {
        //given
        var changePasswordRequest = new ChangePasswordRequest("P@ssw0rd");
        Mockito.doThrow(new InvalidPasswordException(PASSWORD_SHOULD_NOT_BE_THE_SAME.getMessage()))
                .when(accountService)
                .changeAccountPassword(any(), any());

        //when
        mockMvc.perform(patch("/api/v2/accounts/password")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                //then
                .andExpect(status().isBadRequest());
    }


    @Test
    void SaveImageToAccount_FileTypeIsNeitherJpegNorPng_Returns404HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
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
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                        .with(csrf())
                )
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void SaveImageToAccount_FileExtensionIsNeitherJpegNorPng_Returns404HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.gif",
                MediaType.IMAGE_JPEG_VALUE,
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
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .file(image)
                        .with(csrf())
                )
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void SaveImageToAccount_ImageInRequestBodyIsNull_Returns404HttpStatusCode() throws Exception {
        //given
        given(imageService.saveImageToAccount(any(), any(Account.class), any()))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .with(csrf())
                )
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void SaveImageToAccount_ContentTypeIsNeitherJpegNorPng_Returns404HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
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
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                        .with(csrf())
                )
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void SaveImageToAccount_JpegImageIsSent_Returns200HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
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
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                        .with(csrf())
                )
                //then
                .andExpect(status().isCreated());
    }

    @Test
    void SaveImageToAccount_PngImageIsSent_Returns200HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.png",
                MediaType.IMAGE_PNG_VALUE,
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
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .file(image)
                        .file(jsonImageProperties)
                        .with(csrf())
                )
                //then
                .andExpect(status().isCreated());
    }

    @Test
    void DeleteImageFromAccount_ImageIdIsNotBlank_Returns204HttpStatusCode() throws Exception {
        //given
        var imageId = new UUID(1, 1);
        given(account.getId())
                .willReturn(new UUID(1, 1));
        //when
        mockMvc.perform(delete("/api/v2/accounts/images/{imageId}", imageId)
                        .with(csrf())
                        .with(user(account))
                )
                //then
                .andExpect(status().isNoContent());

    }
}