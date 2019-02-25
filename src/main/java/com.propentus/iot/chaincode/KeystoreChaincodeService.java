package com.propentus.iot.chaincode;

import com.google.gson.reflect.TypeToken;
import com.propentus.common.util.EntityUtil;
import com.propentus.iot.BlockchainConnector;
import com.propentus.iot.chaincode.model.OrganisationChaincodeTO;
import com.propentus.iot.chaincode.model.OrganisationsChaincodeTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Offers methods for calling Keystore Smart contract
 */
public class KeystoreChaincodeService extends AbstractChaincodeService {

    private static final Log logger = LogFactory.getLog(UBLChaincodeService.class);

    private static final String CHAIN_CODE_NAME = "Keystore";
    private static final String CHAIN_CODE_PATH = "chaincode/Keystore";
    private static final String CHAIN_CODE_VERSION = "1.1";

    private static final String CHAIN_CODE_METHOD_GET_ORGANISATION = "getOrganisation";
    private static final String CHAIN_CODE_METHOD_ADD_ORGANISATION = "addOrganisation";
    private static final String CHAIN_CODE_METHOD_GET_ORGANISATIONS = "getOrganisations";


    public KeystoreChaincodeService(BlockchainConnector connector) {
        super(connector, new ChaincodeRequestFactory(CHAIN_CODE_NAME, CHAIN_CODE_PATH, CHAIN_CODE_VERSION));
    }

    /**
     * Call function "addOrganisation" on Keystore Smart contract. Argument order:
     * 1. Organisation MSPID
     * 2. Organisation public key base64 encoded.
     * @param organisation
     */
    public boolean addOrganisation(OrganisationChaincodeTO organisation) {

        logger.debug("Calling UBL Smart contract method:" + CHAIN_CODE_METHOD_ADD_ORGANISATION);


        ArrayList<String> args = new ArrayList<String>();
        args.add(organisation.getMspID());
        args.add(organisation.getPublicKey());

        TransactionProposalRequest request = requestFactory.createTransaction(connector.getUser(), CHAIN_CODE_METHOD_ADD_ORGANISATION, args);

        try {
            String response = this.connector.doTransaction(request);
            logger.debug("Received response:" + response);

            //  If chaincodes are not found, they don't get caught by the Exception
            //  So we need to check if response is null
            if (response == null) {
                return false;
            }

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Call function "getOrganisation" on Keystore Smart contract. Argument order:
     * 1. Organisation MSPID
     */
    public OrganisationChaincodeTO getOrganisation(String mspID) {

        logger.debug("Calling UBL Smart contract method:" + CHAIN_CODE_METHOD_GET_ORGANISATION);


        ArrayList<String> args = new ArrayList<String>();
        args.add(mspID);

        QueryByChaincodeRequest request = requestFactory.createQuery(connector.getUser(), CHAIN_CODE_METHOD_GET_ORGANISATION, args);

        try {
            String response = this.connector.doQuery(request);
            logger.debug("Received response:" + response);
            OrganisationChaincodeTO organisation = EntityUtil.JsonToObject(response, OrganisationChaincodeTO.class);
            return organisation;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public OrganisationsChaincodeTO getOrganisations() {

        logger.debug("Calling UBL Smart contract method:" + CHAIN_CODE_METHOD_GET_ORGANISATIONS);
        ArrayList<String> args = new ArrayList<String>();
        QueryByChaincodeRequest request = requestFactory.createQuery(connector.getUser(), CHAIN_CODE_METHOD_GET_ORGANISATION, args);

        try {
            String response = this.connector.doQuery(request);
            logger.debug("Received response:" + response);
            OrganisationsChaincodeTO organisations = EntityUtil.JsonToObject(response, OrganisationsChaincodeTO.class);
            return organisations;
        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }


}
