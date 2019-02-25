package com.propentus.iot.configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.propentus.iot.BlockchainConnector;
import com.propentus.iot.certificates.CertificateGenerator;
import com.propentus.iot.cmd.CommandLineException;
import com.propentus.iot.cmd.CommandLineRunner;
import com.propentus.smartlog.common.configs.AdminClientConfig;
import com.propentus.common.util.file.FileUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.propentus.iot.cmd.CommandLineRunner.executeCommand;

/**
 * Generates needed .yaml files (crypto-config.yaml and configtx.yaml)
 * so we can dynamically use cryptogen and configtxgen to create certificates
 * and organization-JSON to channel configuration update.
 */
public class ConfigGenerator {
	
    private static final Logger logger = LoggerFactory.getLogger(ConfigGenerator.class);
	private static final String CRYPTO_FILE_NAME = AdminClientConfig.CRYPTO_FILE_NAME;
	private static final String CONFIGTX_FILE_NAME = AdminClientConfig.CONFIGTX_FILE_NAME;
	private static final String CONFIGTXGEN_PATH = AdminClientConfig.CONFIGTXGEN_PATH;

	private String templatePath = "";
	private String newFilesPath = "";
	private String orgName = "";
	private String orgDomain = "";
	private String orgMSP = "";
	private String peerNumber = "0";
	private String peerDomainName = "";

	/**
	 * Creates new ConfigGenerator using parameters and replaces newFilesPath domain-template using given domain
	 * @param orgName
	 * @param orgDomain
	 * @param orgMSP
	 */
	public ConfigGenerator(String orgName, String orgDomain, String orgMSP, String peerDomainName, BlockchainConnector connector) {

	    logger.info("Initializing ConfigGenerator for organization: " + orgName);

		this.orgName = orgName;
		this.orgDomain = orgDomain;
		this.orgMSP = orgMSP;
		this.peerDomainName = peerDomainName;
		this.peerNumber = getPeerNumberFromPeerDomainName();

		AdminClientConfig conf = new AdminClientConfig(connector);

		newFilesPath = conf.getGeneratedOrgPath();
		newFilesPath = this.newFilesPath.replace("{domain}", orgDomain);
		templatePath = conf.getTemplatePath();

		logger.info("New files path: " + newFilesPath);
		logger.info("Template path: " + templatePath);
	}
	
	/**
	 * Generates certificates for new organization.
	 * 
	 * Reads crypto-config.yaml-template from disk and replaces curly-bracket templates with orgName and orgDomain.
	 * Creates new generated crypto-config.yaml file to disk and uses it to generate new certificates to organization using CertificateGenerator.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void generateCryptoConfig() throws IOException, InterruptedException {
		
		//	Get test template from disk and change its parameters
		String cryptoConfigPath = templatePath + CRYPTO_FILE_NAME;
    	String cryptoYaml = FileUtil.readFileAsString(cryptoConfigPath);
    	
    	//	Replace template attributes
    	cryptoYaml = cryptoYaml.replace("{orgName}", this.orgName);
    	cryptoYaml = cryptoYaml.replace("{orgDomain}", this.orgDomain);
    	cryptoYaml = cryptoYaml.replace("{peerNumber}", this.peerNumber);
    	
    	//	Create new folder for crypto if not exists
		Path newCrypto = Paths.get(newFilesPath + CRYPTO_FILE_NAME);
		File orgFolder = new File(newFilesPath);
		orgFolder.mkdirs();
		
		//	Write new crypto-config.yaml to disk
		Files.write(newCrypto, cryptoYaml.getBytes());
	}
	
	/**
	 * Reads configtxYaml-template from disk and fills it with orgName, orgDomain and orgMSP
     *
     * Run configtxgen with -printOrg parameter to get new org JSON
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String generateOrgJson() throws IOException, InterruptedException {
		
		//	Load configtx-template from disk
    	String configtxYaml = FileUtil.readFileAsString(templatePath + CONFIGTX_FILE_NAME);
    	
    	//	Replace template attributes
    	configtxYaml = configtxYaml.replace("{orgName}", this.orgName);
    	configtxYaml = configtxYaml.replace("{orgDomain}", this.orgDomain);
    	configtxYaml = configtxYaml.replace("{orgMSP}", this.orgMSP);
    	configtxYaml = configtxYaml.replace("{peerDomainName}", this.peerDomainName);
    	
    	//  Create new folder for configtx if not exists
		Path newCrypto = Paths.get(newFilesPath + CONFIGTX_FILE_NAME);

		File orgFolder = new File(newFilesPath);
		orgFolder.mkdirs();
		
		//	Write new configtx.yaml to disk
		Files.write(newCrypto, configtxYaml.getBytes());
		
		//	Build environment variable
		String[] envVars = new String[1];
        envVars[0] = "FABRIC_CFG_PATH=" + newFilesPath;
        
        //	Stitch up the command for configtxgen
        String orgJsonPath = newFilesPath + orgName + ".json";

        String jsonGenerateCommand = CONFIGTXGEN_PATH + " -printOrg " + orgMSP;

        //	Run configtxgen to generate new organisation JSON
		String output = CommandLineRunner.executeCommand(envVars, jsonGenerateCommand);

        logger.info("Organization JSON: " + output);

        //  Write config-file to disk.
        FileUtil.writeFile(newFilesPath, orgName + ".json", output, true);

    	return output;
	}

    /**
     * Peer number is used in certificate generation so certificates match the peer number given to configs.
     * @return
     */
    private String getPeerNumberFromPeerDomainName() {
        try {
            String[] peerDomainNameSplit = this.peerDomainName.split("\\.");
            String peerId = peerDomainNameSplit[0];
            String peerNumber = peerId.substring(4);
            if (!peerNumber.isEmpty()) {
                return peerNumber;
            } else {
                throw new RuntimeException("peerNumber can't be empty.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Couldn't parse peerNumber from peerDomainName.");
        }
    }
}
