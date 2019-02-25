/*
 *
 *  * Copyright 2016-2019
 *  *
 *  * Interreg Central Baltic 2014-2020 funded project
 *  * Smart Logistics and Freight Villages Initiative, CB426
 *  *
 *  * Kouvola Innovation Oy, FINLAND
 *  * Region Örebro County, SWEDEN
 *  * Tallinn University of Technology, ESTONIA
 *  * Foundation Valga County Development Agency, ESTONIA
 *  * Transport and Telecommunication Institute, LATVIA
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.propentus.smartlog.security;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Offers methods for AES encryption and decryption. Encrypting keys are generated at random.
 */
public class AesCryptoHandler {

    private static final Logger logger = LoggerFactory.getLogger(AesCryptoHandler.class);

    //Static because we need this for encryption and decryption. Otherwise we would need to keep this value somewhere.
    // SHOULD NOT BE CHANGED EVER! Or else we cannot decrypt older messages
    private static final String INIT_VECTOR = "RandomInitVector";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private String value;
    private String secretKey; //Secret key must be 128 bytes length
    private static String initVector = INIT_VECTOR;

    public AesCryptoHandler(String value) {
        this.value = value;
    }

    public AesCryptoHandler(String value, String key, String initVector) {
        this.value = value;
        this.secretKey = key;
        this.initVector = initVector;
    }

    /**
     * Encrypt plaintext content dynamically. Key is generated randomly.
     * @return Base64 encoded encrypted message.
     */
    public String encrypt() {

        logger.debug("Trying to encrypt data:" + this.value);

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

            //Generate secret key randomly
            this.secretKey = generateRandomKey();

            SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(this.value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException("Couldn't encrypt data for sending! This is fatal and should never happen");
        }
    }

    /**
     * Encrypt plaintext content dynamically. Key is generated randomly.
     * @return Base64 encoded encrypted message.
     */
    public String encrypt(String key) {

        this.secretKey = key;

        logger.info("Trying to encrypt data:" + this.value);

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

            SecretKeySpec skeySpec = new SecretKeySpec(this.secretKey.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(this.value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException("Couldn't encrypt data for sending! This is fatal and should never happen");
        }
    }

    public String getKey() {
        return this.secretKey;
    }

    /**
     * Decrypt content with given AES-key.
     * @param key
     * @param encrypted
     * @return
     */
    public static String decrypt(String key, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Encrypt given plaintext content with given key and init vector.
     * @param key
     * @param initVector
     * @param value
     * @return
     */
    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeBase64String(encrypted));

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Generate password of 128 bit. This means 16 UTF-8 characters.
     * Random key character generator uses ASCII table range 33 - 125
     * @return
     */
    public static String generateRandomKey() {
        String key = "";
        for(int i = 0; i < 16; i++) {
            int randomNum = ThreadLocalRandom.current().nextInt(33, 125);
            char character = (char)Integer.parseInt(String.valueOf(randomNum));
            String keyPart = String.valueOf(character);
            key += keyPart;
        }

        if(key.length() != 16) {
            logger.error("Generated AES was invalid size!");
            logger.error("Key:" + key + " and size:" + key.length());
        }

        return key;
    }

}