package com.propentus.iot.chaincode.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for TranportationChain smart contract.
 */
public class TransportChaincodeTO {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("participants")
    @Expose
    private List<String> participants = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

}
