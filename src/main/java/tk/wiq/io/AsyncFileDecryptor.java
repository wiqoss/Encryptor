package tk.wiq.io;

import tk.wiq.TextEncryptor;
import tk.wiq.crypt.AESKey;
import tk.wiq.crypt.FileCryptographyException;
import tk.wiq.crypt.IvPS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class AsyncFileDecryptor {
    
    private File file;
    private AESKey aesKey;
    private IvPS spec;
    private boolean deleteOnDecrypt = false;
    
    public AsyncFileDecryptor(File file) {
        if (!file.exists()) {
            throw new NullPointerException("File doesnt exist");
        }
        
        if (file.isDirectory()) {
            throw new UnsupportedOperationException("Available only for files, use AsyncDirectoryDecryptor");
        }
        
        this.file = file;
    }
    
    public void setKey(AESKey key) {
        this.aesKey = key;
    }
    
    public void setSpec(IvPS spec) {
        this.spec = spec;
    }
    
    public void deleteOnDecrypt(boolean b) {
        this.deleteOnDecrypt = b;
    }
    
    public boolean isDeleteOnDecrypt() {
        return deleteOnDecrypt;
    }
    
    public AESKey getKey() {
        return aesKey;
    }
    
    public IvPS getSpec() {
        return spec;
    }
    
    public CompletableFuture<Void> decrypt() throws FileCryptographyException {
        if (spec == null || aesKey == null || !file.exists()) {
            throw new FileCryptographyException("IvPS (spec), AESKey is null or file doesn't exists.");
        }
        
        return CompletableFuture.runAsync(() -> {
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fis.read(bytes);
                fis.close();
                String data = TextEncryptor.decrypt(new String(bytes), aesKey, spec).join();
                byte[] decryptedData = Base64.getDecoder().decode(data);
                File newFile = new File(file.getParentFile(), file.getName() + ".decrypted");
                FileOutputStream fos = new FileOutputStream(newFile);
                fos.write(decryptedData);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
