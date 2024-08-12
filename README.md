# Encryptor

### Library for encrypting or decrypting text and files in AES


File decrypt/encrypt example:
```java
import tk.wiq.*;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public static void encryptFile(File file) throws Exception {
    CompletableFuture<AESKey> key = AESKey.createAsynchronously(256);
    CompletableFuture<IvPS> ivPS = IvPS.createAsynchronously(16);
    
    // Any activity here ...
    
    AsyncFileEncryptor encryptor = new AsyncFileEncryptor(file);
    encryptor.deleteOnEncrypt(false); // Should file be deleted after encryption? Default false
    
    key.join();
    ivPS.join();
    
    System.out.println("Key: " + key.get().getKey());
    System.out.println("IvPS: " + ivPS.get().getIv());
    
    encryptor.setSpec(ivPS.get());
    encryptor.setKey(key.get());
    encryptor.encrypt(); // Will be created file with .encrypted extension and encrypted content
}

public static void decryptFile(File file, AESKey key, IvPS ivPS) throws Exception {
    AsyncFileDecryptor decryptor = new AsyncFileDecryptor(file);
    decryptor.setKey(key);
    decryptor.setSpec(ivPS);
    decryptor.decrypt(); // Will be created file with .decrypted extension
}
```

Text encrypt/decrypt example:
```java
import tk.wiq.*;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public static CompletableFuture<String> encrypt(String input) throws Exception {
    CompletableFuture<AESKey> key = AESKey.createAsynchronously(256); // You should use 128 or 256 bits
    CompletableFuture<IvPS> ivPS = IvPS.createAsynchronously(16); // Value must be not higher than 16
    
    // Any other activity ...
    
    key.join();
    ivPS.join();
    
    System.out.println("Key: " + key.get().getKey()); // Key for decrypt. Can be used with AESKey key = new AESKey(key_here)
    System.out.println("IvPS: " + ivPS.get().getIv()); // IvPS for decrypt. Can be used with IvPS ivPS = new IvPS(IvPS_here)
    
    return TextEncryptor.encrypt(input, key.get(), ivPS.get());
}
    
public static CompletableFuture<String> decrypt(String input, AESKey key, IvPS ivPS) {
    return TextEncryptor.decrypt(input, key, ivPS);
}
```

### Full examples you can find at [here](https://github.com/yuiopmju/Encryptor/blob/master/src/test/java/examples/Example.java)



Import with Gradle:
```groovy

repositories {
    maven {
        name = "github"
        url = "https://mvn.pkg.github.com"
    }
}

dependencies {
    implementation "tk.wiq:encryptor:1.0.1"
}
```

Import with maven:
```xml

<repositories>
    <repository>
        <id>github</id>
        <url>https://mvn.pkg.github.com</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>tk.wiq</groupId>
        <artifactId>encryptor</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

Your ~/.m2/settings.xml:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/yuiopmju/Encryptor</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>USERNAME</username>
      <password>TOKEN</password>
    </server>
  </servers>
</settings>
```
