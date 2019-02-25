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

package com.propentus.iot.chaincode;

import com.propentus.iot.BlockchainConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;

import java.util.ArrayList;

/**
 * Abstract class for chaincode services.
 */
public class AbstractChaincodeService {

    private static final Log logger = LogFactory.getLog(AbstractChaincodeService.class);

    protected BlockchainConnector connector;
    protected ChaincodeRequestFactory requestFactory;

    private static final String CHAIN_CODE_METHOD_GET_KEYS = "getKeys";

    public AbstractChaincodeService(BlockchainConnector connector, ChaincodeRequestFactory requestFactory) {
        this.connector = connector;
        this.requestFactory = requestFactory;
    }

    /**
     * Get all saved keys for smart contract service extending this class.
     * @return
     */
    public String getKeys() {

        logger.debug("Calling UBL Smart contract method:" + CHAIN_CODE_METHOD_GET_KEYS);

        //Set empty args array
        ArrayList<String> args = new ArrayList<String>();
        QueryByChaincodeRequest request = requestFactory.createQuery(connector.getUser(), CHAIN_CODE_METHOD_GET_KEYS, args);

        try {
            String response = this.connector.doQuery(request);
            logger.debug("Received response:" + response);
            return  response;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
