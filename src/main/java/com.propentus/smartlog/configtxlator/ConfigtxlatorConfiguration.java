package com.propentus.smartlog.configtxlator;

/**
 * Utility class to get URL's for the Configtxlator.
 *
 * If configtxlator.baseurl application parameter is given, uses that value as a baseurl.
 *
 * Public getURL-methods return url like this, baseUrl + /URI for example:
 * http://localhost:7059/protolator/decode/common.Config
 */
public class ConfigtxlatorConfiguration {

    private static final String DECODE_URI = "/protolator/decode/common.Config";
    private static final String ENCODE_URI = "/protolator/encode/common.Config";
    private static final String CONFIGS_TO_PROTO_URI = "/configtxlator/compute/update-from-configs";

    private static String baseUrl = "http://localhost:7059";
    
    static {
        setBaseUrlFromApplicationParameters();
    }

    /**
     * Returns baseUrl + /DECODE_URI
     * @return
     */
    public static String getDecodeUrl() {
        return baseUrl + DECODE_URI;
    }

    /**
     * Returns baseUrl + /DECODE_URI
     * @return
     */
    public static String getEncodeUrl() {
        return baseUrl + ENCODE_URI;
    }

    /**
     * Returns baseUrl + /CONFIGS_TO_PROTO_URI
     * @return
     */
    public static String getConfigsToProtoUrl() {
        return baseUrl + CONFIGS_TO_PROTO_URI;
    }

    /**
     * Gets new base url from application parameters.
     *
     * If value of the new url is null, do nothing. Otherwise replace baseUrl with the new url.
     */
    private static void setBaseUrlFromApplicationParameters() {

        String newBaseUrl = System.getProperty("configtxlator.baseurl");

        if (newBaseUrl != null) {
            baseUrl = newBaseUrl;
        }
    }
}
