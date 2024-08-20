package tk.wiq.io;

import tk.wiq.TextEncryptor;
import tk.wiq.crypt.AESKey;
import tk.wiq.crypt.FileCryptographyException;
import tk.wiq.crypt.IvPS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class AsyncFileEncryptor {
    
    private File file;
    private AESKey aesKey;
    private IvPS spec;
    private boolean deleteOnEncrypt = false;
    
    public AsyncFileEncryptor(File file) {
        if (!file.exists()) {
            throw new NullPointerException("File doesnt exist");
        }
        
        if (file.isDirectory()) {
            throw new UnsupportedOperationException("Available only for files, use AsyncDirectoryEncryptor.");
        }
        
        this.file = file;
    }
    
    public void setKey(AESKey key) {
        this.aesKey = key;
    }
    
    public void setSpec(IvPS spec) {
        this.spec = spec;
    }
    
    public void deleteOnEncrypt(boolean b) {
        this.deleteOnEncrypt = b;
    }
    
    public boolean isDeleteOnEncrypt() {
        return deleteOnEncrypt;
    }
    
    public AESKey getKey() {
        return aesKey;
    }
    
    public IvPS getSpec() {
        return spec;
    }
    
    public CompletableFuture<Void> encrypt() throws FileCryptographyException {
        if (spec == null || aesKey == null || !file.exists()) {
            throw new FileCryptographyException("IvPS (spec), AESKey is null or file doesn't exists.");
        }
        
        return CompletableFuture.runAsync(() -> {
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fis.read(bytes);
                fis.close();
                String data = TextEncryptor.encrypt(Base64.getEncoder().encodeToString(bytes), aesKey, spec).join();
                File newFile = new File(file.getParentFile(), file.getName() + ".encrypted");
                FileOutputStream fos = new FileOutputStream(newFile);
                fos.write(data.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
