package tk.wiq.crypt;

import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

public class IvPS implements CharSequence {

    private String iv;
    private IvParameterSpec ivParameterSpec;

    public IvPS(String iv) {
        this.iv = iv;
        this.ivParameterSpec = new IvParameterSpec(hexStringToByteArray(iv));
    }

    private IvPS(int size) {
        byte[] ivBytes = new byte[size];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        this.ivParameterSpec = new IvParameterSpec(ivBytes);
        this.iv = initStringIv();
    }

    public static CompletableFuture<IvPS> createAsynchronously(int size) {
        return CompletableFuture.completedFuture(new IvPS(size));
    }

    private String initStringIv() {
        StringBuilder stb = new StringBuilder();
        for (byte b : ivParameterSpec.getIV()) {
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

    public String getIv() {
        return iv;
    }

    public IvParameterSpec getIvParameterSpec() {
        return ivParameterSpec;
    }

    @Override
    public int length() {
        return iv.length();
    }

    @Override
    public char charAt(int i) {
        return iv.charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return iv.subSequence(i, i1);
    }

    @Override
    public String toString() {
        return iv;
    }
}
