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
