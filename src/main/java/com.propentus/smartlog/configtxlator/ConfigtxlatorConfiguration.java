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
