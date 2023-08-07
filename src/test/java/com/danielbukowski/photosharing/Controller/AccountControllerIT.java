package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.ChangePasswordRequest;
import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Exception.AccountAlreadyExistsException;
import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Exception.InvalidPasswordException;
import com.danielbukowski.photosharing.Service.AccountService;
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
    void AddImageToAccount_FileTypeIsNeitherJpegNorPng_Returns404HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new byte[] {}
        );
        given(accountService.saveImageToAccount(eq(image), any(Account.class)))
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
    void AddImageToAccount_FileExtensionIsNeitherJpegNorPng_Returns404HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.gif",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{-1, -40}
        );
        given(accountService.saveImageToAccount(eq(image), any(Account.class)))
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
    void AddImageToAccount_ImageInRequestBodyIsNull_Returns404HttpStatusCode() throws Exception {
        //given
        given(accountService.saveImageToAccount(any(), any(Account.class)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .with(csrf())
                )
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void AddImageToAccount_ContentTypeIsNeitherJpegNorPng_Returns404HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new byte[]{-1, -40}
        );
        given(accountService.saveImageToAccount(eq(image), any(Account.class)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .file(image)
                        .with(csrf())
                )
                //then
                .andExpect(status().isBadRequest());
    }

    //todo: tests an image with type jpeg and png if it passes

    @Test
    void AddImageToAccount_JpegImageIsSent_Returns200HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{-1, -40}
        );

        given(accountService.saveImageToAccount(eq(image), any(Account.class)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .file(image)
                        .with(csrf())
                )
                //then
                .andExpect(status().isCreated());
    }

    @Test
    void AddImageToAccount_PngImageIsSent_Returns200HttpStatusCode() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "myimage.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{-119, 80}
        );

        given(accountService.saveImageToAccount(eq(image), any(Account.class)))
                .willReturn(new UUID(1, 1));

        //when
        mockMvc.perform(multipart("/api/v2/accounts/images")
                        .file(image)
                        .with(csrf())
                )
                //then
                .andExpect(status().isCreated());
    }

    @Test
    void GetImageFromAccount_ImageExistsInAccount_Returns200HttpStatusCode() throws Exception {
        //given
        var imageDto = ImageDto.builder()
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .data(new byte[]{-1, -40})
                .build();
        var accountId = new UUID(1, 1);
        var imageId = new UUID(3, 3);
        given(account.getId())
                .willReturn(accountId);
        given(accountService.getImageFromAccount(accountId, imageId))
                .willReturn(imageDto);

        //when
        mockMvc.perform(get("/api/v2/accounts/images/{imageId}", imageId)
                        .with(user(account))
                )
                //then
                .andExpect(status().isOk());
    }

    @Test
    void GetImageFromAccount_ImageDoesNotExistInAccount_Returns404HttpStatusCode() throws Exception {
        //given
        var accountId = new UUID(1, 1);
        var imageId = new UUID(3, 3);
        given(account.getId())
                .willReturn(accountId);
        given(accountService.getImageFromAccount(accountId, imageId))
                .willThrow(ImageNotFoundException.class);

        //when
        mockMvc.perform(get("/api/v2/accounts/images/{imageId}", imageId)
                        .with(user(account))
                )
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    void DeleteImageFromAccount_AccountExists_Returns204HttpStatus() throws Exception {
        //given
        var accountId = new UUID(1, 1);
        given(account.getId()).willReturn(accountId);
        var imageId = new UUID(2, 2);

        //when
        mockMvc.perform(
                        delete("/api/v2/accounts/images/{imageId}", imageId)
                                .with(csrf())
                                .with(user(account))
                )
                //then
                .andExpect(status().isNoContent());
    }

}