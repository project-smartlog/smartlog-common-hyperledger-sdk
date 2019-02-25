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
