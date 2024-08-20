package tk.wiq.io;

import tk.wiq.crypt.AESKey;
import tk.wiq.crypt.FileCryptographyException;
import tk.wiq.crypt.IvPS;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class AsyncDirectoryEncryptor {
    
    private File file;
    private AESKey aesKey;
    private IvPS spec;
    private boolean deleteOnEncrypt = false;
    
    public AsyncDirectoryEncryptor(File file) {
        if (!file.exists()) {
            throw new NullPointerException("File doesnt exist");
        }
        
        if (!file.isDirectory()) {
            throw new UnsupportedOperationException("Only directories supported, use AsyncFileEncryptor.");
        }
        
        this.file = file;
    }
    
    public IvPS getSpec() {
        return spec;
    }
    
    public void setSpec(IvPS spec) {
        this.spec = spec;
    }
    
    public AESKey getKey() {
        return aesKey;
    }
    
    public void setKey(AESKey key) {
        this.aesKey = key;
    }
    
    public void deleteOnEncrypt(boolean b) {
        deleteOnEncrypt = b;
    }
    
    public boolean isDeleteOnEncrypt() {
        return deleteOnEncrypt;
    }
    
    public CompletableFuture<Void> encrypt() throws FileCryptographyException {
        if (spec == null || aesKey == null || !file.exists()) {
            throw new FileCryptographyException("IvPS (spec), AESKey is null or file doesn't exists.");
        }
        
        return CompletableFuture.runAsync(() -> encryptDirectory(this.file));
    }
    
    private void encryptDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                encryptDirectory(file);
            } else {
                encryptFile(file);
            }
        }
    }
    
    private void encryptFile(File file) {
        try {
            var encryptor = new AsyncFileEncryptor(file);
            encryptor.setKey(aesKey);
            encryptor.setSpec(spec);
            encryptor.encrypt().join();
        } catch (FileCryptographyException e) {
            e.printStackTrace();
        }
    }
}
