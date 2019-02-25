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

import com.propentus.common.exception.BlockchainException;
import com.propentus.common.util.EntityUtil;
import com.propentus.iot.BlockchainConnector;
import com.propentus.iot.chaincode.model.UBLChaincodeTO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

/**
 * Offers methods for calling UBL Smart contract
 */
public class UBLChaincodeService {

    private static final Log logger = LogFactory.getLog(UBLChaincodeService.class);

    private static final String CHAIN_CODE_NAME = "UBL";
    private static final String CHAIN_CODE_PATH = "smartlog_chaincode/UBL";
    private static final String CHAIN_CODE_VERSION = "3.6";

    private static final String CHAIN_CODE_METHOD_GET_KEYS = "getKeys";
    private static final String CHAIN_CODE_METHOD_ADD_MESSAGE = "addMessage";
    private static final String CHAIN_CODE_METHOD_GET_MESSAGE = "getMessage";

    private BlockchainConnector connector;
    private ChaincodeRequestFactory requestFactory;

    public UBLChaincodeService(BlockchainConnector connector) {
        this.connector = connector;
        this.requestFactory = new ChaincodeRequestFactory(CHAIN_CODE_NAME, CHAIN_CODE_PATH, CHAIN_CODE_VERSION);
    }

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

    /**
     * Call function "addMessage" on UBL Smart contract. Argument order:
     * 1. UBLChainTO which formatted to JSON and base64 encoded before sending.
     * @param message
     */
    public void addMessage(UBLChaincodeTO message) throws UnsupportedEncodingException, BlockchainException {

        logger.debug("Calling UBL Smart contract method:" + CHAIN_CODE_METHOD_ADD_MESSAGE);

        ArrayList<String> args = new ArrayList<String>();
        //Convert message object to JSON and base64 encode
        String json = EntityUtil.ObjectToJson(message);
        String base64json = Base64.getEncoder().encodeToString(json.getBytes("UTF-8"));
        args.add(base64json);
        args.add(generateRandomKey());

        TransactionProposalRequest request = requestFactory.createTransaction(connector.getUser(), CHAIN_CODE_METHOD_ADD_MESSAGE, args);

        try {
            String response = this.connector.doTransaction(request);
            logger.debug("Received response:" + response);
        }
        catch (BlockchainException be) {
            be.printStackTrace();
            throw be;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Call function "getMessage" on UBL Smart contract. Argument order:
     * 1. Key for message
     */
    public UBLChaincodeTO getMessage(String key) {

        logger.debug("Calling UBL Smart contract method:" + CHAIN_CODE_METHOD_GET_MESSAGE);


        ArrayList<String> args = new ArrayList<String>();
        args.add(key);

        QueryByChaincodeRequest request = requestFactory.createQuery(connector.getUser(), CHAIN_CODE_METHOD_GET_MESSAGE, args);

        try {
            String response = this.connector.doQuery(request);
            logger.debug("Received response:" + response);
            UBLChaincodeTO chaincodeResponse = EntityUtil.JsonToObject(response, UBLChaincodeTO.class);
            return chaincodeResponse;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Generate random key for document, needs to be generated on the client, because chaincodes need to
     * be deterministic. Chaincode can't needs to always create same output depending from time.
     * @return
     */
    private String generateRandomKey() {
    	
    	String mspId = this.connector.getConfig().organisation.getMspid();
    	String unixMillis = Long.toString(Instant.now().toEpochMilli());
    	String random = generateRandom(10);
    	
    	return mspId + "_" + unixMillis + "_" + random;
    }
    
    /**
     * Generates random nonsense, not really random because we already have mspId and unixMillis to make it unique
     * @param length
     * @return
     */
	public String generateRandom(int length){
		String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		int n = alphabet.length();
	
		String result = "";
		Random r = new Random();
	
		for (int i=0; i<length; i++)
			result = result + alphabet.charAt(r.nextInt(n));
	
		return result;
	}

}
