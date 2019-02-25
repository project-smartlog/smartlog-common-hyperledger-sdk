package com.propentus.smartlog.common.configs;

import com.propentus.iot.BlockchainConnector;

public class AdminClientConfig {

    //  Main path where generated stuff and admin configs/certificates reside
    //Full config path for organisation configuration
    public static final String CONFIG_PATH_FULL = "/etc/fabric-admin/config.json";

    public static final String CRYPTO_FILE_NAME = "crypto-config.yaml";
    public static final String CONFIGTX_FILE_NAME = "configtx.yaml";
    public static final String CONFIGTXGEN_PATH = "configtxgen";   //  Global calling for binary
    public static final String CLIENT_JSON_FILENAME = "config.json";

    public static final String CHANNEL_NAME = "changeme";

    public static final String USERNAME = "admin";
    public static final String PASSWORD = "changeme";

    private String configPath = "/etc/fabric-admin/";

    public AdminClientConfig(BlockchainConnector connector) {
        this.configPath = connector.getConfig().fabricEnvPath;
    }

    /**
     * Returns full template path using configPath + templates/
     * @return
     */
    public final String getTemplatePath() {
        return configPath + "templates/";
    }

    /**
     * Returns full generated path using configPath + generated/
     * @return
     */
    public final String getGeneratedPath() {
        return configPath + "generated/";
    }

    /**
     * Returns full generated path using generatedPath + {domain}-placeholder
     * @return
     */
    public String getGeneratedOrgPath() {
        return getGeneratedPath() + "{domain}/";
    }

    /**
     * Returns full generated path using getGenerateOrgPath + "crypto-config/"
     * @return
     */
    public String getGeneratedOrgCerficateOutputPath() {
        return getGeneratedOrgPath() + "crypto-config/";
    }

    /**
     * Returns full path to org policy file
     * @return
     */
    public String getOrgPolicyPath() {
        return getTemplatePath() + "1_org_policy.js";
    }

    /**
     *
     * @return
     */
    public String getGeneratedOrgCryptoPath() {
        return getGeneratedOrgPath() + CRYPTO_FILE_NAME;
    }

    /**
     * Returns the main config path
     * @return
     */
    public String getConfigPath() {
        return configPath;
    }
}