package com.danielbukowski.photosharing.Account;

import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
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
    public void shouldSaveAccountWhenAccountDoesNotExistInDatabase() {
        //given
        Account account = new Account();
        account.setPassword("password");
        account.setEmail("email@gmail.com");

        //when
        accountRepository.save(account);
        List<Account> accountsInDatabase = accountRepository.findAll();

        //then
        Assertions.assertThat(accountsInDatabase).hasSize(1);
    }

    @Test
    public void shouldFindAccountByEmailIgnoreCasesWhenAccountExistsInDatabase() {
        //given
        Account account = new Account();
        account.setPassword("password");
        account.setEmail("myemail@gmail.com");

        //when
        accountRepository.save(account);
        Optional<Account> expectedAccount = accountRepository.findByEmailIgnoreCase("myemail@gmail.com");

        //then
        assertTrue(expectedAccount.isPresent());
    }

    @Test
    public void shouldFindAccountByEmailIgnoreCasesWhenEmailLetterCaseIsDifferent() {
        //given
        Account account = new Account();
        account.setPassword("password");
        account.setEmail("myemail@gmail.com");

        //when
        accountRepository.save(account);
        Optional<Account> expectedAccount = accountRepository.findByEmailIgnoreCase("MYEMAIL@gmail.com");

        //then
        assertTrue(expectedAccount.isPresent());
    }
}