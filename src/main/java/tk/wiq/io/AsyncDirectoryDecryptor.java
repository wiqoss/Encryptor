package tk.wiq.io;

import tk.wiq.crypt.AESKey;
import tk.wiq.crypt.FileCryptographyException;
import tk.wiq.crypt.IvPS;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class AsyncDirectoryDecryptor {
    
    private File file;
    private AESKey aesKey;
    private IvPS spec;
    private boolean deleteOnDecrypt = false;
    
    public AsyncDirectoryDecryptor(File file) {
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
    
    public void deleteOnDecrypt(boolean b) {
        deleteOnDecrypt = b;
    }
    
    public boolean isDeleteOnDecrypt() {
        return deleteOnDecrypt;
    }
    
    public CompletableFuture<Void> decrypt() throws FileCryptographyException {
        if (spec == null || aesKey == null || !file.exists()) {
            throw new FileCryptographyException("IvPS (spec), AESKey is null or file doesn't exists.");
        }
        
        return CompletableFuture.runAsync(() -> decryptDirectory(this.file));
    }
    
    private void decryptDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                decryptDirectory(file);
            } else {
                decryptFile(file);
            }
        }
    }
    
    private void decryptFile(File file) {
        try {
            var decryptor = new AsyncFileDecryptor(file);
            decryptor.setKey(aesKey);
            decryptor.setSpec(spec);
            decryptor.decrypt();
        } catch (FileCryptographyException e) {
            e.printStackTrace();
        }
    }
}
