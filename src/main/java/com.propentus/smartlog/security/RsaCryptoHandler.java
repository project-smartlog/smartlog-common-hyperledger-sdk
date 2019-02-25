package com.propentus.smartlog.security;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Cryptomanager class for handling all cryptographic functionality
 */
public class RsaCryptoHandler {

    private static final String ENCRYPT_DECRYPT_ALG = "RSA";
    private static final String DEFAULT_ALG = "RSA";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String encrypt(String plainText, KeyPair keyPair) {
        try {
            // Init Cipher
            Cipher encryptor = Cipher.getInstance(ENCRYPT_DECRYPT_ALG);
            encryptor.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] ciphered = encryptor.doFinal(bytes);
            return Base64.encodeBase64String(ciphered);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Decrypt given encrypted text with given keypair. Private key of keypair is used in decryption.
     * @param encyptedText
     * @param keyPair
     * @return
     */
    public static String decrypt(String encyptedText, KeyPair keyPair) {
        try {
            // Decrypt, use private key
            Cipher decryptor = Cipher.getInstance(ENCRYPT_DECRYPT_ALG);
            decryptor.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decrypted = decryptor.doFinal(Base64.decodeBase64(encyptedText));
            return new String(decrypted, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *  Generate certificate dynamically
     */
    public static void generateMessagingCertificates(String path) throws NoSuchAlgorithmException, IOException {

        KeyPairGenerator gen = KeyPairGenerator.getInstance(DEFAULT_ALG);
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();
        saveKeyPair(keyPair, path);
    }

    /**
     *  Generate certificate dynamically with given algorithm.
     */
    public static void generateMessagingCertificates(String path, String alg) throws NoSuchAlgorithmException, IOException {

        KeyPairGenerator gen = KeyPairGenerator.getInstance(alg);
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();
        saveKeyPair(keyPair, path);
    }


    /**
     * Save KeyPair to disk.
     *
     * Save public key as X.509 and private key as PKCS8
     */
    public static void saveKeyPair(KeyPair pair, String outputPath) throws IOException {

        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(outputPath + "public.key");
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();

        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
        fos = new FileOutputStream(outputPath + "private.key");
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }

    public static KeyPair loadKeyPair(String path, String algorithm) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        // Read Public Key.
        File filePublicKey = new File(path + "/public.key");
        FileInputStream fis = new FileInputStream(path + "/public.key");
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Read Private Key.
        File filePrivateKey = new File(path + "/private.key");
        fis = new FileInputStream(path + "/private.key");
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

}
