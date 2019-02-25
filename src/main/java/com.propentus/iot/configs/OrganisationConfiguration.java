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

package com.propentus.iot.configs;

import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.EntityUtil;
import com.propentus.common.util.annotation.AllowNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents config model for single organisation configuration. This includes organisation information and also everything Fabric related for organisation.
 */
public class OrganisationConfiguration {

    public enum PeerType {
        NORMAL,
        ENDORSEMENT,
        CLOUD
    }

    public enum AuthType {
        CERT,
        BASIC
    }

    private static final Logger logger = LoggerFactory.getLogger(EntityUtil.class);

    public List<Peer> peers = new ArrayList<Peer>();
    public Orderer orderer;
    public Eventhub eventhub;
    public Organisation organisation;
    public String fabricEnvPath;
    public String caUrl;
    public PeerType peerType;
    public AuthType authType;
    public String channel;
    public String couchDbUrl;

    public String privateKeyPath;

    public String publicKeyPath;

    //Cloud PeerType configs
    //CouchDB username and password. Only needed in Cloud mode.
    public String couchDbUsername;
    public String couchDbPassword;

    public String cloudKeyPath;
    public boolean installed = false;


    /**
     * Check if this configuration is for cloud environment
     * @return
     */
    public boolean isCloudInstallation() {
        return this.peerType == PeerType.CLOUD;
    }

    /**
     * Default constructor
     */
    public OrganisationConfiguration() {}

    /**
     * Builder constructor
     * @param builder
     */
    private OrganisationConfiguration(OrganisationConfigurationBuilder builder) {
        this.peers = builder.peers;
        this.orderer = builder.orderer;
        this.eventhub = builder.eventhub;
        this.organisation = builder.organisation;
        this.fabricEnvPath = builder.fabricEnvPath;
        this.caUrl = builder.caUrl;
        this.peerType = builder.peerType;
        this.channel = builder.channel;
        this.privateKeyPath = builder.privateKeyPath;
        this.publicKeyPath = builder.publicKeyPath;
        this.couchDbUrl = builder.couchDbUrl;
        this.couchDbUsername = builder.couchDbUsername;
        this.couchDbPassword = builder.couchDbPassword;

        this.cloudKeyPath = builder.cloudKeyPath;

    }

    /**
     * Use GSON to create JSON of the entity
     *
     * @return String
     */
    public String toJson() {
        String json = EntityUtil.ObjectToJson(this);
        logger.debug("Created OrganisationConfiguration JSON:" + json);
        return json;
    }

    public static class Peer {
        String url;
        String domainName;

        public Peer(String url, String domainName) {
            this.url = url;
            this.domainName = domainName;
        }
    }

    public static class Orderer {
        String url;
        String domainName;

        public Orderer(String url, String domainName) {
            this.url = url;
            this.domainName = domainName;
        }
    }

    public static class Eventhub {
        String url;
        String domainName;

        public Eventhub(String url, String domainName) {
            this.url = url;
            this.domainName = domainName;
        }
    }

    public static class Organisation {
        String name;
        String mspid;
        String domainName;

        public Organisation(String name, String domainName, String mspid) {
            this.name = name;
            this.domainName = domainName;
            this.mspid = mspid;
        }

        public String getMspid() {
            return mspid;
        }
    }

    /**
     * Builder for OrganisationConfiguration
     */
    public static class OrganisationConfigurationBuilder extends AbstractConfiguration {

        private List<Peer> peers = new ArrayList<Peer>();
        private Orderer orderer;
        private Eventhub eventhub;
        private Organisation organisation;
        private String fabricEnvPath;
        private String caUrl;
        private PeerType peerType;
        private String channel;

        private String couchDbUrl;

        private String privateKeyPath;
        private String publicKeyPath;

        //CouchDB username and password. Only needed in Cloud mode.
        @AllowNull
        public String couchDbUsername;
        @AllowNull
        public String couchDbPassword;
        @AllowNull
        public String cloudKeyPath;

        public OrganisationConfigurationBuilder setPeers(List<Peer> peers) {
            this.peers = peers;
            return this;
        }

        public OrganisationConfigurationBuilder addPeer(Peer peer) {
            this.peers.add(peer);
            return this;
        }

        public OrganisationConfigurationBuilder setOrderer(Orderer orderer) {
            this.orderer = orderer;
            return this;
        }

        public OrganisationConfigurationBuilder setEventhub(Eventhub eventhub) {
            this.eventhub = eventhub;
            return this;
        }

        public OrganisationConfigurationBuilder setOrganisation(Organisation organisation) {
            this.organisation = organisation;
            return this;
        }

        public OrganisationConfigurationBuilder setFabricEnvPath(String fabricEnvPath) {
            this.fabricEnvPath = fabricEnvPath;
            return this;
        }

        public OrganisationConfigurationBuilder setCaUrl(String caUrl) {
            this.caUrl = caUrl;
            return this;
        }

        public OrganisationConfigurationBuilder setPeerType(PeerType peerType) {
            this.peerType = peerType;
            return this;
        }

        public OrganisationConfigurationBuilder setChannelName(String channel) {
            this.channel = channel;
            return this;
        }

        public OrganisationConfigurationBuilder setPublicKey(String publicKey) {
            this.publicKeyPath = publicKey;
            return this;
        }

        public OrganisationConfigurationBuilder setPrivateKey(String privateKey) {
            this.privateKeyPath = privateKey;
            return this;
        }

        public OrganisationConfigurationBuilder setCouchDbUrl(String couchDbUrl) {
            this.couchDbUrl = couchDbUrl;
            return this;
        }

        public OrganisationConfigurationBuilder setCouchDbUsername(String username) {
            this.couchDbUsername = username;
            return this;
        }

        public OrganisationConfigurationBuilder setCouchDbPassword(String password) {
            this.couchDbPassword = password;
            return this;
        }

        public OrganisationConfigurationBuilder setCloudKeyPath(String cloudKeyPath) {
            this.cloudKeyPath = cloudKeyPath;
            return this;
        }

        public OrganisationConfiguration build() throws ConfigurationException {
            OrganisationConfiguration config = new OrganisationConfiguration(this);
            //Validate that all given values were set
            validate();
            return config;
        }
    }
}
