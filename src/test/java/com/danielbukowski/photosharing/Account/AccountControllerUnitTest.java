package com.danielbukowski.photosharing.Account;

import com.danielbukowski.photosharing.Controller.AccountController;
import com.danielbukowski.photosharing.Dto.AccountDto;
import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = AccountController.class)
@WithMockUser
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerUnitTest {

    private final Faker faker = new Faker();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;

    @Test
    public void shouldReturnStatusOkWhenMethodGetAccountsIsCalled() throws Exception {
        //given
        AccountDto accountDto1 = AccountDto.builder()
                .id(new UUID(0, 0))
                .email(faker.internet().emailAddress())
                .build();

        AccountDto accountDto2 = AccountDto.builder()
                .id(new UUID(1, 1))
                .email(faker.internet().emailAddress())
                .build();

        //when
        when(accountService.getAccounts())
                .thenReturn(List.of(accountDto1, accountDto2));
        //then
        mockMvc
                .perform(get("/api/v1/accounts"))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(2)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatusOkWhenMethodGetAccountById() throws Exception {
        //given
        AccountDto accountDto = AccountDto.builder()
                .id(new UUID(0, 0))
                .email(faker.internet().emailAddress())
                .build();

        //when
        when(accountService.getAccountById(accountDto.id()))
                .thenReturn(accountDto);
        //then
        mockMvc
                .perform(get("/api/v1/accounts/{id}", accountDto.id()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(accountDto.id().toString())))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatusBadRequestWhenFieldsAreEmpty() throws Exception {
        //given
        AccountRegisterRequest accountRegisterRequest = new AccountRegisterRequest("", "");

        //then
        mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(accountRegisterRequest))
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath(
                                        "$.fieldNames.password",
                                        Matchers.hasSize(6))
                )
                .andExpectAll(
                        MockMvcResultMatchers.jsonPath(
                                "$.fieldNames.password").value(
                                Matchers.containsInAnyOrder(
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
                        MockMvcResultMatchers
                                .jsonPath(
                                        "$.fieldNames.email",
                                        Matchers.hasSize(1))

                )
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath(
                                        "$.fieldNames.email[0]",
                                        Matchers.is("Should not be empty"))

                );
    }

    @ParameterizedTest
    @ValueSource(strings = {"email.com", "email@.com", "@gmail.com", "email@@gmail.com", "email@gmail."})
    public void shouldReturnStatusBadRequestWhenFieldEmailDoesNotMatchEmailPattern(String email) throws Exception {
        //then
        mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "email":"%s"
                                        }
                                        """.formatted(email))
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath(
                                        "$.fieldNames.email",
                                        Matchers.contains("must be a well-formed email address"))
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "aaaaaaa7", "aaaa!3A", "aaaaa!3", "aaaa!aA", "aaaaa3A"})
    public void shouldReturnStatusBadRequestWhenFieldPasswordIsInvalid(String password) throws Exception {
        //then
        mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                        "password":"%s"
                                        }
                                        """.formatted(password))
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath(
                                        "$.fieldNames.password",
                                        Matchers.anything())
                );
    }
}