package com.danielbukowski.photosharing.Service;

import com.danielbukowski.photosharing.Dto.AccountRegisterRequest;
import com.danielbukowski.photosharing.Dto.ChangePasswordRequest;
import com.danielbukowski.photosharing.Dto.ImageDto;
import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Image;
import com.danielbukowski.photosharing.Entity.Role;
import com.danielbukowski.photosharing.Enum.ExceptionMessageResponse;
import com.danielbukowski.photosharing.Exception.AccountAlreadyExistsException;
import com.danielbukowski.photosharing.Exception.ImageNotFoundException;
import com.danielbukowski.photosharing.Exception.InvalidPasswordException;
import com.danielbukowski.photosharing.Mapper.ImageMapper;
import com.danielbukowski.photosharing.Repository.AccountRepository;
import com.danielbukowski.photosharing.Repository.ImageRepository;
import com.danielbukowski.photosharing.Repository.RoleRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private final Faker faker = new Faker();
    @InjectMocks
    private AccountService accountService;
    @Mock
    private EmailVerificationTokenService emailVerificationTokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ImageMapper imageMapper;
    @Mock
    private S3Service s3Service;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Clock clock;

    private final ZonedDateTime now = ZonedDateTime.of(
            2023,
            6,
            7,
            21,
            37,
            0,
            0,
            ZoneId.of("GMT")
    );

    @Test
    void CreateAccount_AccountAlreadyExists_ThrowsException() {
        //given
        var alreadyExistingEmailInDatabase = faker.internet().emailAddress();
        var accountRegisterRequest = new AccountRegisterRequest(alreadyExistingEmailInDatabase
                , "password123");
        given(accountRepository.existsByEmailIgnoreCase(alreadyExistingEmailInDatabase))
                .willReturn(true);

        //when
        var actualException = assertThrows(
                AccountAlreadyExistsException.class,
                () -> accountService.createAccount(accountRegisterRequest)
        );

        //then
        assertEquals(ExceptionMessageResponse.ACCOUNT_WITH_ALREADY_EXISTING_EMAIL.getMessage(),
                actualException.getMessage()
        );
        then(accountRepository).should(times(0))
                .save(any(Account.class));
    }

    @Test
    void CreateAccount_AccountDoesNotExist_ReturnsId() {
        //given
        var email = faker.internet().emailAddress();
        var role = new Role();
        role.setName("USER");
        var password = faker.internet().password();
        var accountRegisterRequest = new AccountRegisterRequest(email, password);
        given(roleRepository.getByName("USER"))
                .willReturn(role);

        given(accountRepository.save(any(Account.class))
        ).willReturn(Account.builder()
                .id(new UUID(1, 1))
                .password(password)
                .email(email)
                .build()
        );


        //when
        var actualId = accountService.createAccount(accountRegisterRequest);

        //then
        assertEquals(
                new UUID(1, 1),
                actualId
        );
    }

    @Test
    void DeleteAccountById_AccountExists_DeletesAccount() {
        //given
        var accountId = new UUID(1, 1);

        //when
        accountService.deleteAccountById(accountId);

        //then
        then(imageRepository).should(times(1))
                .deleteByAccountId(accountId);
        then(accountRepository).should(times(1))
                .deleteById(accountId);
        then(s3Service).should(times(1))
                .deleteAllImagesFromS3(accountId);
    }

    @Test
    void ChangeAccountPassword_PasswordsAreTheSame_ThrowsException() {
        //given
        var account = Account.builder()
                .password(faker.internet().password())
                .email(faker.internet().emailAddress())
                .build();
        var changePasswordRequest = new ChangePasswordRequest(account.getPassword());
        given(passwordEncoder.matches(
                changePasswordRequest.newPassword(), account.getPassword())
        ).willReturn(true);

        //when
        var actualException = assertThrowsExactly(
                InvalidPasswordException.class,
                () -> accountService.changeAccountPassword(account, changePasswordRequest)
        );

        //then
        assertEquals(
                ExceptionMessageResponse.PASSWORD_SHOULD_NOT_BE_THE_SAME.getMessage(),
                actualException.getMessage()
        );
        then(accountRepository).should(times(0))
                .updatePasswordById(any(), any());
    }

    @Test
    void ChangeAccountPassword_PasswordAreDifferent_ChangesAccountPassword() {
        //given
        var account = Account.builder()
                .id(new UUID(1, 1))
                .password(faker.internet().password())
                .email(faker.internet().emailAddress())
                .build();
        var changePasswordRequest = new ChangePasswordRequest(account.getPassword());
        given(passwordEncoder.matches(changePasswordRequest.newPassword(),
                account.getPassword())
        ).willReturn(false);
        given(passwordEncoder.encode(eq(account.getPassword()))
        ).willReturn(account.getPassword());

        //when
        assertDoesNotThrow(
                () -> accountService.changeAccountPassword(account, changePasswordRequest)
        );

        //then
        then(accountRepository).should(times(1))
                .updatePasswordById(anyString(), any(UUID.class));
    }

    @Test
    void SaveImageToAccount_ImageIsSaved_ReturnsId() {
        //given
        when(clock.instant()).thenReturn(now.toInstant());
        when(clock.getZone()).thenReturn(now.getZone());
        var multipartFile = new MockMultipartFile(
                "myImage",
                "myImage",
                "image/jpg",
                new byte[]{}
        );
        var account = Account.builder()
                .id(new UUID(1, 1))
                .build();
        given(imageRepository.save(any(Image.class)))
                .willReturn(
                        Image.builder()
                                .id(new UUID(2, 2))
                                .build()
                );

        //when
        var actualImageId = accountService.saveImageToAccount(multipartFile, account);

        //then
        assertEquals(
                new UUID(2, 2), actualImageId
        );
        then(s3Service).should(times(1))
                .saveImageToS3(any(), any(), any()
                );
    }

    @Test
    void GetImageFromAccount_CouldNotFindImageInAccount_ThrowsImageNotFoundException() {
        //given
        var accountId = new UUID(2, 2);
        var imageId = new UUID(3, 3);
        given(imageRepository.findByImageIdAndAccountId(imageId, accountId))
                .willReturn(Optional.empty());

        //when
        var actualException = assertThrows(
                ImageNotFoundException.class,
                () -> accountService.getImageFromAccount(accountId, imageId)
        );

        //then
        assertEquals(
                ExceptionMessageResponse.IMAGE_NOT_FOUND.getMessage(),
                actualException.getMessage()
        );
        then(s3Service).should(times(0))
                .getImageFromS3(any(), any());
        then(imageMapper).should(times(0))
                .fromImageToImageDto(any(), any());
    }

    @Test
    void GetImageFromAccount_FoundImageFromTheDatabase_ReturnsImageDto() {
        //given
        var accountId = new UUID(2, 2);
        var imageId = new UUID(3, 3);
        var image = Image.builder()
                .title(faker.animal().name())
                .contentType("image/jpg")
                .account(
                        Account.builder()
                                .id(new UUID(1, 1))
                                .build()
                )
                .build();
        given(imageRepository.findByImageIdAndAccountId(imageId, accountId))
                .willReturn(Optional.of(image));
        var imageInBytes = new byte[]{};
        given(s3Service.getImageFromS3(accountId, imageId))
                .willReturn(imageInBytes);
        given(imageMapper.fromImageToImageDto(imageInBytes, image))
                .willReturn(ImageDto.builder()
                        .data(imageInBytes)
                        .contentType(image.getContentType())
                        .build()
                );

        //when
        var actualImageDto = accountService.getImageFromAccount(accountId, imageId);

        //then
        assertEquals(
                image.getContentType(), actualImageDto.contentType()
        );
        assertEquals(
                imageInBytes, actualImageDto.data()
        );
    }

    @Test
    void DeleteImageFromAccount_ImageExistsInTheDatabase_DeletesImage() {
        //given
        var accountId = new UUID(1, 1);
        var imageId = new UUID(2, 2);

        //when
        accountService.deleteImageFromAccount(accountId, imageId);

        //then
        then(s3Service).should(times(1))
                .deleteImageFromS3(accountId, imageId);
        then(imageRepository).should(times(1))
                .deleteById(imageId);
    }

}