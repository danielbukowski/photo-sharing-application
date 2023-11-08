package com.danielbukowski.photosharing.Repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class EmailVerificationTokenRepositoryIT {

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:alpine");
    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @DynamicPropertySource
    public static void setupContainer(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }


    @Sql(
            scripts = "classpath:db/populate_test_data.sql",
            statements = "INSERT INTO email_verification_tokens(email_verification_token_id, account_id, expiration_date) " +
                    "VALUES ('368b8a28-0cc4-4d8f-b8ca-19e7e10ec16e', '4e280c33-518e-444f-a541-0cc4b14b5b05', NOW()); " +
                    "INSERT INTO email_verification_tokens(email_verification_token_id, account_id, expiration_date) " +
                    "VALUES ('c96c0426-6b26-46f8-a1c8-eadeafaefdd5', '6d1afd8a-f8cf-4dd9-b105-fd4b9f81a8eb', NOW()); "
    )
    @Test
    void FindByAccountId_AccountExists_FindsTheToken() {
        //given
        var accountId = UUID.fromString("4e280c33-518e-444f-a541-0cc4b14b5b05");
        var expectedTokenId = UUID.fromString("368b8a28-0cc4-4d8f-b8ca-19e7e10ec16e");

        //when
        var actualToken = emailVerificationTokenRepository.findByAccountId(accountId);

        //then
        assertTrue(actualToken.isPresent());
        assertEquals(expectedTokenId, actualToken.get().getId());
    }

    @Sql(
            scripts = "classpath:db/populate_test_data.sql",
            statements = "INSERT INTO email_verification_tokens(email_verification_token_id, account_id, expiration_date) " +
                    "VALUES ('368b8a28-0cc4-4d8f-b8ca-19e7e10ec16e', '4e280c33-518e-444f-a541-0cc4b14b5b05', NOW()); " +
                    "INSERT INTO email_verification_tokens(email_verification_token_id, account_id, expiration_date) " +
                    "VALUES ('c96c0426-6b26-46f8-a1c8-eadeafaefdd5', '6d1afd8a-f8cf-4dd9-b105-fd4b9f81a8eb', NOW()); "
    )
    @Test
    void FindByAccountId_AccountDoesNotExist_DoesNotFindToken() {
        //given
        var accountId = UUID.fromString("6e28a1c8-518e-444f-a541-0cc4b14b5b05");

        //when
        var actualToken = emailVerificationTokenRepository.findByAccountId(accountId);

        //then
        assertTrue(actualToken.isEmpty());
    }

}