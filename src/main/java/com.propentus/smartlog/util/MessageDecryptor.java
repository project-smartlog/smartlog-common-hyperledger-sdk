package com.propentus.smartlog.util;

import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.DataFormatter;
import com.propentus.iot.BlockchainConnector;
import com.propentus.iot.chaincode.model.UBLChaincodeTO;
import com.propentus.iot.configs.ConfigReader;
import com.propentus.iot.configs.OrganisationConfiguration;
import com.propentus.smartlog.datasource.couchdb.entities.ApiUser;
import com.propentus.smartlog.security.AesCryptoHandler;
import com.propentus.smartlog.security.CryptoUtil;
import com.propentus.smartlog.security.RsaCryptoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Decrypts encrypted UBL Messages to Strings
 */
public class MessageDecryptor {

    private static final Logger logger = LoggerFactory.getLogger(MessageDecryptor.class);

    /* Template is:
        {cloudPrivateKeyPath}/{domainName}/crypto-config/private.key
     */
    private static final String CLOUD_PRIVATE_KEY_PATH_TEMPLATE = "{0}/{1}/crypto-config/private.key";

    private BlockchainConnector connector;

    /**
     * Constructor
     * @param connector
     */
    public MessageDecryptor(BlockchainConnector connector) {
        this.connector = connector;
    }

    /**
     * Takes list of UBLChaincodeTO's as an argument, gets organizations private key from disk
     * and tries to decrypt its secret key with it. If decrypt is successful, the decrypted key is then used
     * to decrypt the whole message.
     *
     * @param messages
     * @param user
     *
     * @return List of decrypted messages
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeySpecException
     */
    public List<String> decrypt(List<UBLChaincodeTO> messages, ApiUser user) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, ConfigurationException {

        logger.debug("Decrypting crypted messages back to UBL");

        List<String> messageList = new ArrayList<String>();

        //Check for PeerType configuration, if CLOUD type, create full path. Otherwise use configured 'privateKeyPath'.
        ConfigReader configReader = new ConfigReader();
        OrganisationConfiguration organisationConfiguration = configReader.getOrganisationConfiguration();

        PrivateKey privateKey = null;
        String privateKeyPath = null;
        if(organisationConfiguration.isCloudInstallation()) {

            if(user == null) {
                logger.error("ApiUser cannot be null, when querying data in cloud mode.");
                return null;
            }

            privateKeyPath = createCloudPrivateKeyPath(organisationConfiguration.cloudKeyPath, user.getDomainName());
        }
        else {
            privateKeyPath = connector.getConfig().privateKeyPath;
        }

        //Key path solved, load the private key now
        privateKey = CryptoUtil.loadPrivateKey(privateKeyPath, "RSA");
        KeyPair keyPair = new KeyPair(null, privateKey);

        String ownMsp = connector.getConfig().organisation.getMspid();

        if(organisationConfiguration.isCloudInstallation()) {
            ownMsp = user.getOrganisation();
        }

        for (UBLChaincodeTO message : messages) {

            for (UBLChaincodeTO.Participant p : message.getParticipants()) {

                //  find yourself in participants so we get the right decryptkey
                if (p.getMSPID().equals(ownMsp)) {
                    String decryptedKey = RsaCryptoHandler.decrypt(p.getEncryptedKey(), keyPair);
                    String ubl = AesCryptoHandler.decrypt(decryptedKey, message.getEncryptedMessage());
                    messageList.add(ubl);
                    break;
                }
            }
        }

        return messageList;
    }

    /**
     * Takes list of UBLChaincodeTO's as an argument, gets organizations private key from disk
     * and tries to decrypt its secret key with it. If decrypt is successful, the decrypted key is then used
     * to decrypt the whole message.
     *
     * @param messages
     * @param user
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeySpecException
     */
    public List<UBLChaincodeTO> decryptAndGetAsUBLChaincodeTOList(List<UBLChaincodeTO> messages, ApiUser user) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, ConfigurationException {

        logger.debug("Decrypting crypted messages back to UBL");

        List<UBLChaincodeTO> messageList = new ArrayList<UBLChaincodeTO>();

        //Check for PeerType configuration, if CLOUD type, create full path. Otherwise use configured 'privateKeyPath'.
        ConfigReader configReader = new ConfigReader();
        OrganisationConfiguration organisationConfiguration = configReader.getOrganisationConfiguration();

        PrivateKey privateKey = null;
        String privateKeyPath = null;
        if(organisationConfiguration.isCloudInstallation()) {

            if(user == null) {
                logger.error("ApiUser cannot be null, when querying data in cloud mode.");
                return null;
            }

            privateKeyPath = createCloudPrivateKeyPath(organisationConfiguration.cloudKeyPath, user.getDomainName());
        }
        else {
            privateKeyPath = connector.getConfig().privateKeyPath;
        }

        //Key path solved, load the private key now
        privateKey = CryptoUtil.loadPrivateKey(privateKeyPath, "RSA");
        KeyPair keyPair = new KeyPair(null, privateKey);

        String ownMsp = connector.getConfig().organisation.getMspid();

        if(organisationConfiguration.isCloudInstallation()) {
            ownMsp = user.getOrganisation();
        }

        for (UBLChaincodeTO message : messages) {

            for (UBLChaincodeTO.Participant p : message.getParticipants()) {

                //  find yourself in participants so we get the right decryptkey
                if (p.getMSPID().equals(ownMsp)) {
                    String decryptedKey = RsaCryptoHandler.decrypt(p.getEncryptedKey(), keyPair);
                    message.decryptedMessage = AesCryptoHandler.decrypt(decryptedKey, message.getEncryptedMessage());
                    messageList.add(message);
                    break;
                }
            }
        }

        return messageList;
    }

    /**
     * Create full file path to organisation's private key, in cloud mode.
     * File path template is: '{cloudPrivateKeyPath}/{domainName}/crypto-config/private.key'
     * Where cloudPrivateKeyPath is value from OrganisationConfiguration, behind key: cloudPrivateKeyPath
     * and domainName is organisations configured domain name.
     *
     * For example: domainName
     * @return Full path to private key file.
     */
    private String createCloudPrivateKeyPath(String cloudPrivateKeyPath, String domainName) {
        logger.debug("Constructing full path to organisation's private key. cloudPrivateKeyPath: '{}'. domainName: '{}'", cloudPrivateKeyPath, domainName);
        String fullPath = DataFormatter.fillTemplate(CLOUD_PRIVATE_KEY_PATH_TEMPLATE, cloudPrivateKeyPath, domainName);
        logger.debug("Created path: '{}'", fullPath);
        return fullPath;
    }
}
