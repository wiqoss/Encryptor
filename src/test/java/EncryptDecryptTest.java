import org.junit.jupiter.api.Test;
import tk.wiq.AESKey;
import tk.wiq.AsyncEncryptor;
import tk.wiq.IvPS;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class EncryptDecryptTest {

    public Long encryptAndDecrypt(String input) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, InterruptedException {
        long e = System.currentTimeMillis();
        CompletableFuture<AESKey> key = AESKey.createAsynchronously(256);
        CompletableFuture<IvPS> spec = IvPS.createAsynchronously(16);
        key.join();
        spec.join();
        CompletableFuture<String> encryptedText = AsyncEncryptor.encrypt(input, key.get(), spec.get());
        //System.out.println("Waiting for encrypt end...");
        encryptedText.join();
        //System.out.println("Encrypted in " + (System.currentTimeMillis() - e));
        //long d = System.currentTimeMillis();
        CompletableFuture<String> decryptedText = AsyncEncryptor.decrypt(encryptedText.get(), key.get(), spec.get());
        //System.out.println("Waiting for decrypt end...");
        decryptedText.join();
        //System.out.println("Decrypted in " + (System.currentTimeMillis() - d));
        long time = (System.currentTimeMillis() - e);
        //System.out.println("Process finished in " + getColor(time) + " ms for \"" + input + "\"");
        //System.out.println("Process finished in " + getColor(time) + " ms");
        return time;
//        System.out.println(
//                "Encrypted text: " + encryptedText.get() +
//                "\nDecrypted text: " + decryptedText.get() +
//                "\nSecret key: " + key.get().getKey() +
//                "\nIvParameterSpec: " + spec.get().getIv()
//        );
    }

    private String getColor(long time) {
        if (time >= 23) {
            return "\033[31m" + time + "\033[0m";
        } else if (time >= 20) {
            return "\033[33m" + time + "\033[0m";
        } else {
            return "\033[32m" + time + "\033[0m";
        }
    }

    @Test
    public void encryptAndDecryptTest() {
        List<Long> results = new ArrayList<>();
        try {
            int i = 0;
            while (i != 100_000) {
                StringBuilder stb = new StringBuilder();
                int j = 0;
                while (j != 10_000) {
                    stb.append(ThreadLocalRandom.current().nextInt(10_000));
                    j++;
                }

                results.add(encryptAndDecrypt(stb.toString()));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Average time: " + results.stream().mapToDouble(x -> x).average().getAsDouble());
            System.out.println("First time: " + results.getFirst());
        }));
    }
}
