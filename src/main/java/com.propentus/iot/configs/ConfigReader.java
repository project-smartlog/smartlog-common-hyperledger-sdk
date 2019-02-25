package com.propentus.iot.configs;

import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.EntityUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Configuration reader class for reading .json file from local filesystem. Configure organisationConfigPath for your local environment, or otherwise your network won't work.
 */
public class ConfigReader {

    private Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    //Configure this for your local environment
    //Can be overriden in application parameters
    private static String organisationConfigPath  = "/etc/hyperledger/ext_config/config.json";
    //Organisation related configs
    private OrganisationConfiguration organisationConfiguration;
    
    static {
    	setOrganisationConfigPathFromApplicationParameter();
    }

    public ConfigReader(String organisationConfigPath) throws ConfigurationException {
        loadConfigs(organisationConfigPath);
    }

    public ConfigReader() throws ConfigurationException {
        loadConfigs(organisationConfigPath);
    }

    /**
     * Try to load OrganisationConfiguration values from config.json
     */
    private void loadConfigs(String organisationConfigPath) throws ConfigurationException {

        //Try to load organisation configurations
        try {
            logger.info("Trying to load organisation.json from: '{}'", organisationConfigPath);
            String organisationJson = readFile(organisationConfigPath);
            this.organisationConfiguration = EntityUtil.JsonToObject(organisationJson, OrganisationConfiguration.class);
        } catch (Exception e) {
            logger.error("Error reading Organisation configuration file from path:" + organisationConfigPath, e);
            throw new ConfigurationException("Error reading Organisation configuration file from path:" + organisationConfigPath, e);
        }

    }

    /**
     * If config path is given in application parameter replace the default with it.
     * If config path is not found from application parameters, do nothing.
     */
    private static void setOrganisationConfigPathFromApplicationParameter() {

        String newPath = System.getProperty("smartlog.configpath");

        if (newPath != null) {
            organisationConfigPath = newPath;
        }
    }

    public OrganisationConfiguration getOrganisationConfiguration() {
        return this.organisationConfiguration;
    }

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.forName("UTF-8"));
    }



}
