package com.muzlive.kitpage.kitpage.config.encryptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
public class AesSecurityProvider {

    @Value("${security.publicKey}")
    private String publicKey;

    @Value("${security.secretKey}")
    private String secretKey;

    public SecretKeySpec generateKey(String key) {
        try {
            return new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException occurred while generating a key.", e);
        }
        return null;
    }

    public String encrypt(String dataToEncrypt) throws Exception {
        return encrypt(secretKey, publicKey, dataToEncrypt);
    }

    public String encrypt(String secretKey, String publicKey, String dataToEncrypt) throws Exception {
        try {
            if(ObjectUtils.isEmpty(dataToEncrypt)) return "";

            IvParameterSpec iv = new IvParameterSpec(publicKey.getBytes("UTF-8"));
            SecretKeySpec skeySpec = generateKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(dataToEncrypt.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException occurred while encrypting.", e);
            throw e;
        } catch (NoSuchPaddingException e) {
            log.error("NoSuchPaddingException occurred while encrypting.", e);
            throw e;
        } catch (InvalidKeyException e) {
            log.error("InvalidKeyException occurred while encrypting.", e);
            throw e;
        } catch (InvalidAlgorithmParameterException e) {
            log.error("InvalidAlgorithmParameterException occurred while encrypting.", e);
            throw e;
        } catch (IllegalBlockSizeException e) {
            log.error("IllegalBlockSizeException occurred while encrypting.", e);
            throw e;
        } catch (BadPaddingException e) {
            log.error("BadPaddingException occurred while encrypting.", e);
            throw e;
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException occurred while encrypting.", e);
            throw e;
        }
    }

    public String decrypt(String encryptedData) throws Exception {
        return decrypt(secretKey, publicKey, encryptedData);
    }

    public String decrypt(String secretKey, String publicKey, String encryptedData) throws Exception {
        try {
            if(ObjectUtils.isEmpty(encryptedData)) return "";

            IvParameterSpec iv = new IvParameterSpec(publicKey.getBytes("UTF-8"));
            SecretKeySpec skeySpec = generateKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(original);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException occurred while decrypting.", e);
            throw e;
        } catch (NoSuchPaddingException e) {
            log.error("NoSuchPaddingException occurred while decrypting.", e);
            throw e;
        } catch (InvalidKeyException e) {
            log.error("InvalidKeyException occurred while decrypting.", e);
            throw e;
        } catch (InvalidAlgorithmParameterException e) {
            log.error("InvalidAlgorithmParameterException occurred while decrypting.", e);
            throw e;
        } catch (IllegalBlockSizeException e) {
            log.error("IllegalBlockSizeException occurred while decrypting.", e);
            throw e;
        } catch (BadPaddingException e) {
            log.error("BadPaddingException occurred while decrypting.", e);
            throw e;
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException occurred while decrypting.", e);
            throw e;
        }
    }

    public byte[] encrypt(byte[] dataToEncrypt) throws Exception {
        return encrypt(secretKey, publicKey, dataToEncrypt);
    }

    public byte[] encrypt(String secretKey, String publicKey, byte[] dataToEncrypt) throws Exception {
        try {
            IvParameterSpec iv = new IvParameterSpec(publicKey.getBytes("UTF-8"));
            SecretKeySpec skeySpec = generateKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return Base64.getEncoder().encode(cipher.doFinal(dataToEncrypt));
        } catch (Exception e) {
            log.error("Exception occurred while encrypting.", e);
            throw e;
        }
    }

    public InputStream encryptAndBase64(String secretKey, String publicKey, InputStream inputStream) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(publicKey.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec skeySpec = generateKey(secretKey);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        InputStream cipherStream = new CipherInputStream(inputStream, cipher);

        // Base64 인코딩을 OutputStream으로 하므로 직접 래핑은 어려움 → 메모리에 저장 필요
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        OutputStream base64Output = Base64.getEncoder().wrap(byteArrayOutput);

        byte[] buffer = new byte[4096];
        int n;
        while ((n = cipherStream.read(buffer)) != -1) {
            base64Output.write(buffer, 0, n);
        }
        base64Output.close(); // 반드시 닫아야 Base64 마무리됨

        return new ByteArrayInputStream(byteArrayOutput.toByteArray());
    }

    public byte[] decrypt(byte[] dataToEncrypt) throws Exception {
        return decrypt(secretKey, publicKey, dataToEncrypt);
    }

    public byte[] decrypt(String secretKey, String publicKey, byte[] encryptedData) throws Exception {
        try {
            IvParameterSpec iv = new IvParameterSpec(publicKey.getBytes("UTF-8"));
            SecretKeySpec skeySpec = generateKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        } catch (Exception e) {
            log.error("Exception occurred while encrypting.", e);
            throw e;
        }
    }
}
