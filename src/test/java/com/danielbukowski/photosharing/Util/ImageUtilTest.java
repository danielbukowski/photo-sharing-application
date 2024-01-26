package com.danielbukowski.photosharing.Util;

import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ImageUtilTest {

    @InjectMocks
    private ImageUtil imageUtil;

    @Test
    void HasAccessToImage_ImageIsPrivateAndAccountIsNull_ReturnsFalse() {
        //given
        var image = Image.builder()
                .account(Account.builder()
                        .id(new UUID(1, 1))
                        .build())
                .isPrivate(true)
                .build();
        Account account = null;

        //when
        var result = imageUtil.hasAccessToImage(account, image);

        //then
        assertFalse(result);
    }

    @Test
    void HasAccessToImage_ImageIsPrivateAndAccountIsDifferent_ReturnsFalse() {
        //given
        var image = Image.builder()
                .account(Account.builder()
                        .id(new UUID(1, 1))
                        .build())
                .isPrivate(true)
                .build();
        Account account = Account.builder()
                .id(new UUID(2, 2))
                .build();

        //when
        var result = imageUtil.hasAccessToImage(account, image);

        //then
        assertFalse(result);
    }

    @Test
    void HasAccessToImage_ImageIsPrivateAndAccountIsTheSame_ReturnsTrue() {
        //given
        var image = Image.builder()
                .account(Account.builder()
                        .id(new UUID(1, 1))
                        .build())
                .isPrivate(true)
                .build();
        Account account = Account.builder()
                .id(new UUID(1, 1))
                .build();

        //when
        var result = imageUtil.hasAccessToImage(account, image);

        //then
        assertTrue(result);
    }

    @Test
    void HasAccessToImage_ImageIsNotPrivateAndAccountIsNull_ReturnsTrue() {
        //given
        var image = Image.builder()
                .account(Account.builder()
                        .id(new UUID(1, 1))
                        .build())
                .isPrivate(false)
                .build();
        Account account = null;

        //when
        var result = imageUtil.hasAccessToImage(account, image);

        //then
        assertTrue(result);
    }

    @Test
    void HasAccessToImage_ImageIsNotPrivateAndAccountIsDifferent_ReturnsTrue() {
        //given
        var image = Image.builder()
                .account(Account.builder()
                        .id(new UUID(1, 1))
                        .build())
                .isPrivate(false)
                .build();
        Account account = Account.builder()
                .id(new UUID(2,2))
                .build();

        //when
        var result = imageUtil.hasAccessToImage(account, image);

        //then
        assertTrue(result);
    }

    @Test
    void HasAccessToImage_ImageIsNotPrivateAndAccountIsTheSame_ReturnsTrue() {
        //given
        var image = Image.builder()
                .account(Account.builder()
                        .id(new UUID(1, 1))
                        .build())
                .isPrivate(false)
                .build();
        Account account = Account.builder()
                .id(new UUID(1,1))
                .build();

        //when
        var result = imageUtil.hasAccessToImage(account, image);

        //then
        assertTrue(result);
    }

}