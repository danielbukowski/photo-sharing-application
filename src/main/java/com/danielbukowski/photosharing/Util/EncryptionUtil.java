package com.danielbukowski.photosharing.Util;

import com.danielbukowski.photosharing.Property.EncryptionProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

@Component
public class EncryptionUtil {

    private final static  String ALGORITHM = "AES";
    private final static int ALGORITHM_KEY_SIZE = 256;
    private final static int IV_BYTE_LENGTH = 12;
    private final static int PBKDF2_ITERATIONS = 8931;
    private final SecretKey key;
    private final EncryptionProperties encryptionProperties;

    public EncryptionUtil(EncryptionProperties encryptionProperties) {
        this.encryptionProperties = encryptionProperties;
        this.key = generateKey();
    }

    public SecretKey generateKey() {
        try {
            KeySpec spec = new PBEKeySpec(encryptionProperties.getPassword().toCharArray(),
                    encryptionProperties.getSalt().getBytes(),
                    PBKDF2_ITERATIONS,
                    ALGORITHM_KEY_SIZE);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] key = f.generateSecret(spec).getEncoded();

            return new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encrypt(byte[] data) {
        try {
            SecureRandom rand = new SecureRandom();
            byte[] iv = new byte[IV_BYTE_LENGTH];
            rand.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(key.getEncoded(), ALGORITHM),
                    new GCMParameterSpec(128, iv)
            );

            byte[] cipherData = cipher.doFinal(data);
            byte[] ivAndCipherText = new byte[iv.length + cipherData.length];
            System.arraycopy(iv, 0, ivAndCipherText, 0, iv.length);
            System.arraycopy(cipherData, 0, ivAndCipherText, iv.length, cipherData.length);

            return ivAndCipherText;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decrypt(byte[] data) {
        try {
            byte[] iv = new byte[IV_BYTE_LENGTH];
            byte[] cipherData = new byte[data.length - IV_BYTE_LENGTH];
            System.arraycopy(data, 0, iv, 0, iv.length);
            System.arraycopy(data, iv.length, cipherData, 0, cipherData.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(key.getEncoded(), ALGORITHM),
                    new GCMParameterSpec(128, iv)
            );

            return cipher.doFinal(cipherData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
