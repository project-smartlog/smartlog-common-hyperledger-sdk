package com.propentus.iot.chaincode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.User;

import java.util.ArrayList;

public class ChaincodeRequestFactory {

	private static final Log logger = LogFactory.getLog(ChaincodeRequestFactory.class);

    private String chaincodeName;
    private String chaincodePath;
    private String chaincodeVersion;

    public ChaincodeRequestFactory(String chaincodeName, String chaincodePath, String chaincodeVersion) {
        this.chaincodeName = chaincodeName;
        this.chaincodePath = chaincodePath;
        this.chaincodeVersion = chaincodeVersion;
    }

    public QueryByChaincodeRequest createQuery(User user, String function, ArrayList<String> args) {
        QueryByChaincodeRequest request = QueryByChaincodeRequest.newInstance(user);
        //Set parameters to request
        //////////////////////////

        //Set chaincodeID
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(this.chaincodeName)
                .setVersion(this.chaincodeVersion)
                .setPath(this.chaincodePath).build();
        request.setChaincodeID(chaincodeID);

        //Set function to be called on Smart-Contract
        request.setFcn(function);

        //Set args array
        request.setArgs(args);

        return request;
    }

    public TransactionProposalRequest createTransaction(User user, String function, ArrayList<String> args) {

        TransactionProposalRequest request = TransactionProposalRequest.newInstance(user);

        //Set chaincodeID
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(this.chaincodeName)
                .setVersion(this.chaincodeVersion)
                .setPath(this.chaincodePath).build();
        request.setChaincodeID(chaincodeID);

        request.setFcn(function);
        request.setArgs(args);
        request.setProposalWaitTime(120000);
        request.setUserContext(user);

        logger.debug("Created transaction proposal");
        logger.debug("Function: " + function + ", Args:" + args);

        return request;
    }

}
