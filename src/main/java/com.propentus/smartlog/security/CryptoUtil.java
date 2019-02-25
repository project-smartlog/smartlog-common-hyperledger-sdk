package com.propentus.smartlog.security;

import com.propentus.common.util.StringUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Utility class for different Cryptographic methods
 */
public class CryptoUtil {

    //Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
    private static final String ENCRYPT_DECRYPT_ALG = "RSA";
    private static final String DEFAULT_ALG = "RSA";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    /**
     *  Generate certificates dynamically
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
    public static KeyPair generateMessagingCertificates(String path, String alg) throws NoSuchAlgorithmException, IOException {

        KeyPairGenerator gen = KeyPairGenerator.getInstance(alg);
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();
        saveKeyPair(keyPair, path);
        return keyPair;
    }


    /**
     * Save KeyPair to disk.
     *
     * Save public key as X.509 and private key as PKCS8
     */
    private static void saveKeyPair(KeyPair pair, String outputPath) throws IOException {

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

    public static KeyPair loadKeyPair(String publicKeyPath, String privateKeyPath, String algorithm) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        // Read Public Key.
        File filePublicKey = new File(publicKeyPath);
        FileInputStream fis = new FileInputStream(publicKeyPath);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Read Private Key.{
        File filePrivateKey = new File(privateKeyPath);
        fis = new FileInputStream(privateKeyPath);
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

    /**
     * Try to load public key from given path. Path must be full path to key file.
     * @param path
     * @param algorithm
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey loadPublicKey(String path, String algorithm) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        // Read Public Key.
        File filePublicKey = new File(path);
        FileInputStream fis = new FileInputStream(path);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();


        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);

        return keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * Try to load private key from given path. Path must be full path to key file.
     * @param path
     * @param algorithm
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey loadPrivateKey(String path, String algorithm) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        // Read private key
        File filePublicKey = new File(path);
        FileInputStream fis = new FileInputStream(path);
        byte[] encodedPrivateKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();


        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);

        return keyFactory.generatePrivate(privateKeySpec);
    }

    public static KeyPair base64ToKeyPair(String base64key) throws IOException, ClassNotFoundException {


        byte[] base64 = Base64.getDecoder().decode(base64key);

        ByteArrayInputStream bi = new ByteArrayInputStream(base64);
        ObjectInputStream oi = new ObjectInputStream(bi);
        Object obj = oi.readObject();

        KeyPair keyPair = null;
        if(obj instanceof KeyPair) {
            keyPair = (KeyPair)obj;
        }

        oi.close();
        bi.close();
        return keyPair;
    }

    public static PublicKey base64ToPublicKey(String base64key) throws IOException, ClassNotFoundException {


        byte[] base64 = Base64.getDecoder().decode(base64key);

        ByteArrayInputStream bi = new ByteArrayInputStream(base64);
        ObjectInputStream oi = new ObjectInputStream(bi);
        Object obj = oi.readObject();

        PublicKey publicKey = null;
        if(obj instanceof PublicKey) {
            publicKey = (PublicKey)obj;
        }

        oi.close();
        bi.close();
        return publicKey;
    }

    /**
     * Converts keypair to base64 format String.
     * @param keyPair
     * @return
     * @throws Exception
     */
    public static String keypairToString(KeyPair keyPair) throws Exception {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o =  new ObjectOutputStream(b);
        o.writeObject(keyPair);
        byte[] res = b.toByteArray();
        o.close();
        b.close();

        return Base64.getEncoder().encodeToString(res);
    }

    /**
     * Converts PublicKey to base64 format String.
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String publicKeyToString(PublicKey publicKey) throws Exception {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o =  new ObjectOutputStream(b);
        o.writeObject(publicKey);
        byte[] res = b.toByteArray();
        o.close();
        b.close();

        return Base64.getEncoder().encodeToString(res);
    }


}
