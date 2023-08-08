package com.danielbukowski.photosharing.Repository;

import com.danielbukowski.photosharing.Entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Sql("classpath:db/populate_test_data.sql")
@Testcontainers
@DataJpaTest
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AccountRepositoryIT {

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:alpine");
    @Autowired
    private AccountRepository accountRepository;

    @DynamicPropertySource
    public static void setupContainer(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    public void checkInitiatedTestData() {
        assertEquals(2, accountRepository.findAll().size());
    }

    @Test
    void UpdatePasswordById_AccountExists_ChangePasswordAccount() {
        //given
        var password = "myNewPassword";
        var accountId = UUID.fromString("4e280c33-518e-444f-a541-0cc4b14b5b05");

        //when
        accountRepository.updatePasswordById(password, accountId);

        //then
        var account = accountRepository.getById(accountId);
        assertEquals(password, account.getPassword());

        //Checks if other account's password was not changed
        assertEquals("ca44tSs!!", accountRepository.getById(UUID.fromString("6d1afd8a-f8cf-4dd9-b105-fd4b9f81a8eb")).getPassword());
    }

    @Test
    void UpdatePasswordById_AccountDoesNotExist_DoNotChangeAnyPasswordAccount() {
        //given
        var password = "myNewPassword";
        var accountId = UUID.fromString("5f280c33-518e-444f-a541-0cc4b14b5b05");

        //when
        accountRepository.updatePasswordById(password, accountId);

        //then
        for (Account account : accountRepository.findAll()) {
            assertNotEquals(password, account.getPassword());
        }
    }

    @Test
    void FindByEmailIgnoreCase_EmailIsExactlyTheSame_FindsAccount() {
        //given
        var email = "iLoveDogs@gmail.com";
        //when
        var expectedResult = accountRepository.findByEmailIgnoreCase(email);
        //then
        assertTrue(expectedResult.isPresent());
    }

    @Test
    void FindByEmailIgnoreCase_EmailIsInDifferentCase_FindsAccount() {
        //given
        var email = "iLoveDOGS@gmail.com";
        //when
        var expectedResult = accountRepository.findByEmailIgnoreCase(email);
        //then
        assertTrue(expectedResult.isPresent());
    }

    @Test
    void FindByEmailIgnoreCase_EmailDoesNotExist_DoesNotFindAccount() {
        //given
        var email = "iLoveDucks@gmail.com";
        //when
        var expectedResult = accountRepository.findByEmailIgnoreCase(email);
        //then
        assertTrue(expectedResult.isEmpty());
    }

    @Test
    void ExistsByEmailIgnoreCase_EmailIsExactlyTheSame_FindsAccount() {
        //given
        var email = "iLoveDogs@gmail.com";
        //when
        var expectedResult = accountRepository.existsByEmailIgnoreCase(email);
        //then
        assertTrue(expectedResult);
    }

    @Test
    void ExistsByEmailIgnoreCase_EmailIsInDifferentCase_FindsAccount() {
        //given
        var email = "iLoveDOGS@gmail.com";
        //when
        var expectedResult = accountRepository.existsByEmailIgnoreCase(email);
        //then
        assertTrue(expectedResult);
    }

    @Test
    void ExistsByEmailIgnoreCase_EmailDoesNotExist_DoesNotFindAccount() {
        //given
        var email = "iLoveDucks@gmail.com";
        //when
        var expectedResult = accountRepository.existsByEmailIgnoreCase(email);
        //then
        assertFalse(expectedResult);
    }

}