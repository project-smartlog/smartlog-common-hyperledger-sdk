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

package com.propentus.iot.chaincode.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Model for UBL-messages in UBL smart contract. Contains encrypted message and information about participants, who can decrypt this message.
 */
public class UBLChaincodeTO {

    public enum FullEmptyIndicator {
        EMPTY,
        FULL,
    }

    //  Contains the pure unhandled message
    public transient String decryptedMessage;

    //  Contains the encrypted message
    private String encryptedMessage;

    //  List of participants, participant contains MSPID and secret key
    public List<Participant> participants = new ArrayList<Participant>();

    //  ID for this message.
    private String documentID;

    //  MSP of the organisation that sent the message
    private String organisationID;

    //  The Smartlog supply chain id (provided by Propentus). Required for each transaction to decide which
    //  organisations should have access to the data.
    private String supplyChainID;

    //  Intermodal shipping container id in ISO 6346 compliant format.
    private String containerID;

    //  The identification of the party sending the message.
    private String senderParty;

    //  An identifier for use in tracing this piece of transport equipment (for example an intermodal shipping
    //  container), such as the EPC number used in RFID.
    private String RFIDTransportEquipment;

    //  An identifier for use in tracing this transport handling unit (for example a train or a wagon),
    //  such as the EPC number used in RFID.
    private String RFIDTransportHandlingUnit;

    //  A code/text signifying the type of this transport event.
    private String statusTypeCode;

    //  In format "YYYY-MM-DD HH:MM:SS+TIMEZONE".
    private String timestamp;

    //  Reference number assigned by a carrier to identify a specific shipment, such as a booking reference number.
    private String carrierAssignedID;

    //  Reference number to identify a Shipping Order.
    private String shippingOrderID;

    //  Information about the container status “EMPTY” or “FULL”.
    private FullEmptyIndicator emptyFullIndicator;



    /**
     * Smartlog API field updates
     */

    //  The content type standard used in the original message. For example "UBL" or "GS1".
    private String contentType;

    //  The standard version of the used content type.
    private String contentTypeSchemeVersion;

    // Status location identifier, for example RFID reader id or a company id/name.
    private String statusLocationId;



    /**
     * Setters and getters
     */

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentTypeSchemeVersion() {
        return contentTypeSchemeVersion;
    }

    public void setContentTypeSchemeVersion(String contentTypeSchemeVersion) {
        this.contentTypeSchemeVersion = contentTypeSchemeVersion;
    }

    public String getStatusLocationId() {
        return statusLocationId;
    }

    public void setStatusLocationId(String statusLocationId) {
        this.statusLocationId = statusLocationId;
    }

    public void setDecryptedMessage(String decryptedMessage) {this.decryptedMessage = decryptedMessage; }

    public String getDecryptedMessage() { return this.decryptedMessage; }

    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getOrganisationID() {
        return organisationID;
    }

    public void setOrganisationID(String organisationID) {
        this.organisationID = organisationID;
    }

    public String getSupplyChainID() {
        return supplyChainID;
    }

    public void setSupplyChainID(String supplyChainID) {
        this.supplyChainID = supplyChainID;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getSenderParty() {
        return senderParty;
    }

    public void setSenderParty(String senderParty) {
        this.senderParty = senderParty;
    }

    public String getRFIDTransportEquipment() {
        return RFIDTransportEquipment;
    }

    public void setRFIDTransportEquipment(String RFIDTransportEquipment) {
        this.RFIDTransportEquipment = RFIDTransportEquipment;
    }

    public String getRFIDTransportHandlingUnit() {
        return RFIDTransportHandlingUnit;
    }

    public void setRFIDTransportHandlingUnit(String RFIDTransportHandlingUnit) {
        this.RFIDTransportHandlingUnit = RFIDTransportHandlingUnit;
    }

    public String getStatusTypeCode() {
        return statusTypeCode;
    }

    public void setStatusTypeCode(String statusTypeCode) {
        this.statusTypeCode = statusTypeCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCarrierAssignedID() {
        return carrierAssignedID;
    }

    public void setCarrierAssignedID(String carrierAssignedID) {
        this.carrierAssignedID = carrierAssignedID;
    }

    public String getShippingOrderID() {
        return shippingOrderID;
    }

    public void setShippingOrderID(String shippingOrderID) {
        this.shippingOrderID = shippingOrderID;
    }

    public FullEmptyIndicator getEmptyFullIndicator() {
        return emptyFullIndicator;
    }

    public void setEmptyFullIndicator(FullEmptyIndicator emptyFullIndicator) {
        this.emptyFullIndicator = emptyFullIndicator;
    }

    public static class Participant {
        public String getMSPID() {
            return MSPID;
        }

        public void setMSPID(String MSPID) {
            this.MSPID = MSPID;
        }

        private String MSPID;

        public String getEncryptedKey() {
            return encryptedKey;
        }

        public void setEncryptedKey(String encryptedKey) {
            this.encryptedKey = encryptedKey;
        }

        private String encryptedKey;
    }

}
