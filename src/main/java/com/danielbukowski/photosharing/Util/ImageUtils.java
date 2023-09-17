package com.danielbukowski.photosharing.Util;

import com.danielbukowski.photosharing.Entity.Account;
import com.danielbukowski.photosharing.Entity.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


@Slf4j
@Component
public class ImageUtils {

    private static final int BUFFER_SIZE = 1024 * 4;

    public byte[] compressImage(byte[] image) {
        try {
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            deflater.setInput(image);
            deflater.finish();

            ByteArrayOutputStream out = new ByteArrayOutputStream(image.length);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (!deflater.finished()) {
                int len = deflater.deflate(buffer);
                out.write(buffer, 0, len);
            }

            deflater.end();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Could not compress an image", e);
            throw new RuntimeException(e);
        }
    }

    public byte[] decompressImage(byte[] compressedImage) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(compressedImage);

            ByteArrayOutputStream out = new ByteArrayOutputStream(compressedImage.length);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (!inflater.finished()) {
                int len = inflater.inflate(buffer);
                out.write(buffer, 0, len);
            }

            inflater.end();
            out.close();
            return out.toByteArray();
        } catch (IOException | DataFormatException e) {
            log.error("Could not decompress an image", e);
            throw new RuntimeException(e);
        }
    }

    public boolean hasAccessToImage(Account account, Image image) {
        return !image.isPrivate() || image.getAccount().equals(account);
    }

}
