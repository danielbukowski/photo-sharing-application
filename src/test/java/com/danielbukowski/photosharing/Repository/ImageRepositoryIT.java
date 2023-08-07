package com.danielbukowski.photosharing.Repository;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql("classpath:db/populate_test_data.sql")
@Testcontainers
@DataJpaTest
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ImageRepositoryIT {

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:alpine");
    @Autowired
    private ImageRepository imageRepository;
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
        assertEquals(4, imageRepository.findAll().size());
        assertEquals(2, accountRepository.findAll().size());
    }

    @Test
    void DeleteByAccountId_AccountExistWithImages_DeletesOnlyImagesWithTheAccountId() {
        //when
        imageRepository.deleteByAccountId(UUID.fromString("4e280c33-518e-444f-a541-0cc4b14b5b05"));

        //then
        assertEquals( 2, imageRepository.findAll().size());
        assertEquals( 2,accountRepository.findAll().size());
    }

    @Test
    void DeleteByAccountId_AccountIdDoesNotExist_DoesNotDeleteAnyImage() {
        //given
        UUID notExistingAccountIdInTheDatabase = UUID.fromString("5e280c33-518e-444f-a541-0cc4b14b5b05");

        //when
        imageRepository.deleteByAccountId(notExistingAccountIdInTheDatabase);

        //then
        assertEquals( 4, imageRepository.findAll().size());
        assertEquals( 2,accountRepository.findAll().size());
    }

    @Test
    void FindByImageIdAndAccountId_ImageAndAccountDoNotExit_DoesNotFindAnyImage() {
        //given
        var imageId = UUID.fromString("af008884-3492-4ead-94e9-8e674ea8cfca");
        var accountId = UUID.fromString("8e280c33-518e-444f-a541-0cc4b14b5b05");

        //when
        var expectedImage = imageRepository.findByImageIdAndAccountId(
                imageId, accountId
        );

        //then
        assertTrue(expectedImage.isEmpty());
    }

    @Test
    void FindByImageIdAndAccountId_ImageAndAccountDoExistButTheyAreNotRelated_DoesNotFindAnyImage() {
        //given
        var imageId = UUID.fromString("4e280c33-518e-444f-a541-0cc4b14b5b05");
        var accountId = UUID.fromString("7e5cb8bf-a089-4d47-9dc0-24c3b628533f");

        //when
        var expectedImage = imageRepository.findByImageIdAndAccountId(
                imageId, accountId
        );

        //then
        assertTrue(expectedImage.isEmpty());
    }

    @Test
    void FindByImageIdAndAccountId_ImageAndAccountDoExistAndTheyAreRelated_DoesFindImage() {
        //given
        var imageId =  UUID.fromString("bf008884-3492-4ead-94e9-8e674ea8cfca");
        var accountId = UUID.fromString("4e280c33-518e-444f-a541-0cc4b14b5b05");

        //when
        var expectedImage = imageRepository.findByImageIdAndAccountId(
                imageId, accountId
        );

        //then
        assertTrue(expectedImage.isPresent());
    }

}