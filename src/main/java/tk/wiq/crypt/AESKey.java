package tk.wiq.crypt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

public class AESKey implements CharSequence {

    private String key;
    private SecretKey secretKey;

    public AESKey(String key) {
        this.key = key;
        this.secretKey = new SecretKeySpec(hexStringToByteArray(key), "AES");
    }

    private AESKey(int size) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(size);
            this.secretKey = keyGenerator.generateKey();
            this.key = initStringKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<AESKey> createAsynchronously(int size) {
        return CompletableFuture.completedFuture(new AESKey(size));
    }

    private String initStringKey() {
        StringBuilder stb = new StringBuilder();
        for (byte b : secretKey.getEncoded()) {
            stb.append(String.format("%02x", b));
        }

        return stb.toString();
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int length() {
        return key.length();
    }

    @Override
    public char charAt(int i) {
        return key.charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return key.subSequence(i, i1);
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public String toString() {
        return key;
    }
}
