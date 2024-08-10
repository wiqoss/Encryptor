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

    public Long encryptAndDecrypt(String input, boolean log) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, InterruptedException {
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
        if(log) {
            System.out.println("Process finished in " + getColor(time) + " ms");
            System.out.println(
                "Encrypted text: \033[31m" + encryptedText.get() +
                "\n\033[0mSecret key: \033[32m" + key.get().getKey() +
                "\n\033[0mIvParameterSpec: \033[31m" + spec.get().getIv()
            );
        }

        return time;
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

                results.add(encryptAndDecrypt(stb.toString(), false));
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

    @Test
    public void encryptBillionSymbols() {
        String s = "S".repeat(1_000_000);
        try {
            encryptAndDecrypt(s, true);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
