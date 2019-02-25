package com.propentus.iot.chaincode.model;


/**
 * Model for organisation in Keystore smart contract.
 */
public class OrganisationChaincodeTO {

    public String getMspID() {
        return mspID;
    }

    public void setMspID(String mspID) {
        this.mspID = mspID;
    }

    private String mspID;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    private String publicKey;


}
