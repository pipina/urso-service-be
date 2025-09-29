package sk.is.urso.config;

import org.alfa.exception.CommonException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;

@Configuration
public class EncryptionConfig {

    /**
     * String to hold name of the encryption algorithm.
     */
    public static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * String to hold the name of the private key file.
     */
    @Value("${integration.csru.crypt.privateKey.file}")
    public File privateKeyFile;

    @Value("${integration.csru.crypt.lazyInit:#{false}}")
    public boolean lazyInitEncryption;

    /**
     * String to hold name of the public key file.
     */
    @Value("${integration.csru.crypt.publicKey.file}")
    public File publicKeyFile;

    @Value("${integration.csru.crypt.password.file}")
    public File passwordFile;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private BouncyCastleProvider provider;

    public PublicKey getPublicKey() {
        if(publicKey == null){
            return getPublicKeyFromCert();
        }
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        if (privateKey == null){
            return getPrivateKeyFromFile();
        }
        return privateKey;
    }

    public EncryptionConfig() {
    }

    @PostConstruct
    void initCipher() {
        provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        if (!lazyInitEncryption){
            publicKey = getPublicKeyFromCert();
            privateKey = getPrivateKeyFromFile();
        }
    }

//    @Bean(name = "publicKey")
    public PublicKey getPublicKeyFromCert() {
        try (FileInputStream is = new FileInputStream(publicKeyFile)){
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
            return cer.getPublicKey();
        }
        catch (Exception ex){
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Chyba pri načítaní verejného kľúča zo súboru " + publicKeyFile, ex);
        }
    }

//    @Bean(name = "privateKey")
    public PrivateKey getPrivateKeyFromFile() {
    	String password = getPassword();
        try (PEMParser pemParser = new PEMParser(new InputStreamReader(new FileInputStream(privateKeyFile)))){
        	
        	if(password == null || password.isEmpty()) {
            	JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
                return converter.getPrivateKey(privateKeyInfo);
            }
            else {
            	PKCS8EncryptedPrivateKeyInfo encPKInfo = (PKCS8EncryptedPrivateKeyInfo) pemParser.readObject();
            	InputDecryptorProvider decProv = new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider(provider.getName()).build(password.toCharArray());
                PrivateKeyInfo pkInfo = encPKInfo.decryptPrivateKeyInfo(decProv);
                return new JcaPEMKeyConverter().setProvider(provider.getName()).getPrivateKey(pkInfo);
            }
        }
        catch (Exception ex){
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Chyba pri načítaní súkromného kľúča zo súboru " + privateKeyFile + " s password.len = " + password.length() , ex);
        }
    }

    String getPassword() {
        try {
            return Files.readString(passwordFile.toPath(), StandardCharsets.UTF_8).trim();
        }
        catch (Exception ex){
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Chyba pri načítaní hesla", ex);
        }
    }
}
