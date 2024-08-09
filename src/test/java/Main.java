import tk.wiq.AsyncEncryptor;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, InterruptedException {
        String input = "Some very nice and interesting text";
        SecretKey key = AsyncEncryptor.generateKey(128);
        IvParameterSpec spec = AsyncEncryptor.generateIv();
        long e = System.currentTimeMillis();
        CompletableFuture<String> encryptedText = AsyncEncryptor.encrypt(input, key, spec);
        System.out.println("Waiting for encrypt end...");
        encryptedText.join();
        System.out.println("Encrypted in " + (System.currentTimeMillis() - e));
        long d = System.currentTimeMillis();
        CompletableFuture<String> decryptedText = AsyncEncryptor.decrypt(encryptedText.get(), key, spec);
        System.out.println("Waiting for decrypt end...");
        decryptedText.join();
        System.out.println("Decrypted in " + (System.currentTimeMillis() - d));
        System.out.println("Process finished in " + (System.currentTimeMillis() - e));
        System.out.println(
                "Encrypted text: " + encryptedText.get() +
                "\nDecrypted text: " + decryptedText.get() +
                "\nSecret key: " + toString(key.getEncoded()) +
                "\nIvParameterSpec: " + toString(spec.getIV())
        );
    }

    private static String toString(byte[] bytes) {
        StringBuilder stb = new StringBuilder();
        stb.append("[ ");
        for (byte i : bytes) {
            stb.append(i).append(" ");
        }

        return stb.append("]").toString();
    }
}
