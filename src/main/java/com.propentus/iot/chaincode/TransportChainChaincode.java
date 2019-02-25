package com.propentus.iot.chaincode;

import com.propentus.common.exception.BlockchainException;
import com.propentus.common.util.EntityUtil;
import com.propentus.common.util.StringUtil;
import com.propentus.iot.BlockchainConnector;
import com.propentus.iot.chaincode.model.TransportChaincodeTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Offers methods for calling TransportChain Smart contract
 */
public class TransportChainChaincode extends AbstractChaincodeService {

    private static final Logger logger = LoggerFactory.getLogger(TransportChainChaincode.class);

    private static final String CHAIN_CODE_NAME = "TransportChain";
    private static final String CHAIN_CODE_PATH = "chaincode/TransportChain";
    private static final String CHAIN_CODE_VERSION = "2.0";

    private static final String CHAIN_CODE_METHOD_ADD_TRANSPORT_CHAIN  = "setTransportChain";
    private static final String CHAIN_CODE_METHOD_GET_TRANSPORT_CHAIN = "getTransportChain";


    public TransportChainChaincode(BlockchainConnector connector) {
        super(connector, new ChaincodeRequestFactory(CHAIN_CODE_NAME, CHAIN_CODE_PATH, CHAIN_CODE_VERSION));
    }

    /**
     * Call function "addTransportChain" on TransportChain Smart contract. Argument order:
     * 1. Transport chain ID
     * 2. List of participant organisations MSPID
     * @param transportChain
     */
    public boolean addTransportChain(TransportChaincodeTO transportChain) throws BlockchainException {

        logger.debug("Calling UBL Smart contract method:" + CHAIN_CODE_METHOD_ADD_TRANSPORT_CHAIN);

        //Generate organisation part. MSPID's are comma separated
        String orgs = "";
        Iterator<String> iter = transportChain.getParticipants().iterator();
        while(iter.hasNext()) {
            String org = iter.next();
            orgs += org;
            //Dont add dot to last element
            if(iter.hasNext()) {
               orgs += ",";
            }
        }

        ArrayList<String> args = new ArrayList<String>();
        args.add(transportChain.getId());
        args.add(orgs);

        TransactionProposalRequest request = requestFactory.createTransaction(connector.getUser(), CHAIN_CODE_METHOD_ADD_TRANSPORT_CHAIN, args);

        try {
            String response = this.connector.doTransaction(request);
            logger.debug("Received response:" + response);

            if (response == null) {
                logger.info("Got null response");
                return false;
            }

            return true;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    /**
     * Call function "getTransportChain" on TransportChain Smart contract. Argument order:
     * 1. TransportChain ID
     */
    public TransportChaincodeTO[] getTransportChain(String chainID) throws BlockchainException {

        logger.debug("Calling UBL Smart contract method:" + CHAIN_CODE_METHOD_GET_TRANSPORT_CHAIN);


        ArrayList<String> args = new ArrayList<String>();
        args.add(chainID);

        QueryByChaincodeRequest request = requestFactory.createQuery(connector.getUser(), CHAIN_CODE_METHOD_GET_TRANSPORT_CHAIN, args);

        try {
            String response = this.connector.doQuery(request);
            logger.debug("Received response:" + response);

            if (response == null) {
                throw new BlockchainException("Error getting connection to blockchain.");
            }

            //  TransportationChain couldn't be found
            if (response.equals("")) {
                return null;
            }


            if (StringUtil.isEmpty(response)) {
                return null;
            }

            // If smart contract returns only one chaincode, it isn't an array, but if multiple
            // chains are returned those are automatically wrapped to array.
            if (!response.startsWith("[")) {
                response = "[" + response + "]";
            }

            TransportChaincodeTO[] transportChain = EntityUtil.JsonToObject(response, TransportChaincodeTO[].class);
            return transportChain;
        } catch (BlockchainException be) {
            throw be;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
