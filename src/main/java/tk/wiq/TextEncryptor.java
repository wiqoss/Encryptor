package tk.wiq;

import tk.wiq.crypt.AESKey;
import tk.wiq.crypt.IvPS;

import javax.crypto.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class TextEncryptor {
    
    public static CompletableFuture<String> encrypt(String input, AESKey key, IvPS spec) {
        return CompletableFuture.supplyAsync(() -> {
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key.getSecretKey(), spec.getIvParameterSpec());
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }

            try {
                return Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes()));
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CompletableFuture<String> decrypt(String input, AESKey key, IvPS spec) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key.getSecretKey(), spec.getIvParameterSpec());
                byte[] text = cipher.doFinal(Base64.getDecoder().decode(input));
                return new String(text);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
