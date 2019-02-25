package com.propentus.iot.certificates;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Logger;

import com.propentus.common.exception.BlockchainException;
import com.propentus.iot.BlockchainConnector;
import com.propentus.iot.chaincode.KeystoreChaincodeService;
import com.propentus.iot.chaincode.model.OrganisationChaincodeTO;
import com.propentus.iot.cmd.CommandLineRunner;
import com.propentus.smartlog.common.configs.AdminClientConfig;
import com.propentus.smartlog.security.CryptoUtil;
import com.propentus.common.util.file.FileUtil;

/**
 * Used to generate Hyperledger certificates for organization.
 *
 * Uses cryptogen through CommandLineRunner to generate certificates.
 */
public class CertificateGenerator {

    Logger logger = Logger.getLogger("CertificateGenerator");
	
	private String orgDomain = "";
	private String mspid = "";
	private String certOutputPath = "";
	private String cryptoPath = "";

    /**
     * Initializes multiple paths that are used to generate certificates.
     *
     * @param orgDomain
     * @param mspid
     * @param connector
     */
	public CertificateGenerator(String orgDomain, String mspid, BlockchainConnector connector) {
	    this.mspid = mspid;
		this.orgDomain = orgDomain;

		AdminClientConfig adminConfig = new AdminClientConfig(connector);

		cryptoPath = adminConfig.getGeneratedOrgCryptoPath();
		certOutputPath = adminConfig.getGeneratedOrgCerficateOutputPath();
		cryptoPath = cryptoPath.replace("{domain}", orgDomain);
		certOutputPath = certOutputPath.replace("{domain}", orgDomain);
	}

	/**
	 * Deletes previous certificates from organization directory and generates new certificates using
     * cryptogen from command line.
     *
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void generateCertificatesForOrganization() throws IOException, InterruptedException {

	    // First delete old certificates and keystores
        // Avoids having multiple keystores for organization
        // which crashes Hyperledger SDK
        FileUtil.deleteDirectory(certOutputPath);

		String command = "cryptogen generate --config=" + cryptoPath + " --output=" + certOutputPath;

		CommandLineRunner.executeCommand(null, "{0}", command);
	}

    /**
     * Generates messaging keys for organization.
     *
     * Saves private and public key to organization's crypto-config folder and sends public key to blockchain.
     * Public key is used to encrypt data that is sent to blockchain.
     *
     */
    public void generateMessagingCertificates(BlockchainConnector connector) throws Exception {

        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();
        saveKeyPair(keyPair);
        OrganisationChaincodeTO to = new OrganisationChaincodeTO();
        to.setMspID(mspid);

        //  Public key in BASE64
        to.setPublicKey(CryptoUtil.publicKeyToString(keyPair.getPublic()));

        //  Send public key to blockchain
        KeystoreChaincodeService service = new KeystoreChaincodeService(connector);
        if (service.addOrganisation(to)) {
            logger.info("Added organization's (" + mspid + ") PublicKey to blockchain");
        }
        else {
            throw new BlockchainException("Could not add organisation's PublicKey to blockchain");
        }
    }

    /**
     * Save KeyPair to disk.
     *
     * Save public key as X.509 and private key as PKCS8
     */
    public void saveKeyPair(KeyPair pair) throws IOException {

        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(certOutputPath + "public.key");
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();

        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        fos = new FileOutputStream(certOutputPath + "private.key");
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }

    /**
     * Loads PrivateKey and PublicKey specs from disk and uses KeyFactory to
     * generate proper PrivateKey and PublicKey
     *
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public KeyPair loadKeyPair() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        // Read Public Key.
        File filePublicKey = new File(certOutputPath + "public.key");
        FileInputStream fis = new FileInputStream(certOutputPath + "public.key");
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // Read Private Key.
        File filePrivateKey = new File(certOutputPath + "private.key");
        fis = new FileInputStream(certOutputPath + "private.key");
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        // Generate KeyPair.
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }
}
