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
import com.propentus.iot.UserFactory;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdkintegration.SampleOrg;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initialize basic application configurations. Almost all configurations are related to Hyperledger Fabric at the moment.
 */
public class BasicSettingInitializer {

	private static final Logger logger = LoggerFactory.getLogger(BasicSettingInitializer.class);
	
	private HFClient hfclient = null;
	private SampleOrg sampleOrg;
	private UserFactory userFactory;
	private ConfigReader configReader;
	
	public BasicSettingInitializer() throws ConfigurationException {

		try {
			this.configReader = new ConfigReader();
		} catch (ConfigurationException e) {
			logger.error("Error in configurations", e);
			throw new ConfigurationException();
		}

		userFactory = new UserFactory(configReader);
		
		hfclient = HFClient.createNewInstance();
		try {
			hfclient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            sampleOrg = createOrg();
            hfclient.setUserContext(sampleOrg.getPeerAdmin());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	private SampleOrg createOrg() throws IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {

		OrganisationConfiguration configuration = configReader.getOrganisationConfiguration();

		SampleOrg sampleOrg = new SampleOrg(configuration.organisation.name, configuration.organisation.mspid);
		sampleOrg.setDomainName(configuration.organisation.domainName);
		sampleOrg.addOrdererLocation(configuration.orderer.domainName, configuration.orderer.url);
		sampleOrg.addEventHubLocation(configuration.eventhub.domainName, configuration.eventhub.url);
		sampleOrg.setCALocation(configuration.caUrl);

		//Organisation can have multiple peers configured
		for(OrganisationConfiguration.Peer peerConfig : configuration.peers) {
			sampleOrg.addPeerLocation(peerConfig.domainName, peerConfig.url);
		}
		
		//Set CA client
		try {
			sampleOrg.setCAClient(HFCAClient.createNewInstance(sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		
		//Set users for organisation
		try {
			userFactory.setUsers(sampleOrg);
		} catch (
				EnrollmentException
				| org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		try {
			sampleOrg.setPeerAdmin(userFactory.getPeerAdmin(sampleOrg));
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| InvalidKeySpecException | IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		
		return sampleOrg;
	}
	
	public HFClient getHFClient() {
		return this.hfclient;
	}
	
	public SampleOrg getSampleOrg() {
		return this.sampleOrg;
	}

	public ConfigReader getConfigReader() {return this.configReader; }

}
