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

package com.propentus.iot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.propentus.common.util.file.FileUtil;
import com.propentus.smartlog.common.configs.AdminClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Contains methods for all the different ways to edit channel configuration.
 *
 * For example add new organization to channel, remove organization from channel..
 *
 */
public class ConfigEditor {

	private static final Logger logger = LoggerFactory.getLogger(ConfigEditor.class);

	private String configJSON;
	private String orgPolicyPath = "";
	
	public ConfigEditor(String configJSON, BlockchainConnector connector) {
		this.configJSON = configJSON;

		AdminClientConfig conf = new AdminClientConfig(connector);
		orgPolicyPath = conf.getOrgPolicyPath();
	}

	/**
	 * Generates new organisation JSON-element for ConfigTxLator
	 * @param msp
	 * @param newOrgJson
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public String addOrganisation(String msp, String newOrgJson) throws IOException, InterruptedException {

		logger.info("Adding organization: " + msp);
		logger.info("New org JSON: " + newOrgJson);

    	//	Parse configuration and new organisation JSON as a JsonObject
		JsonParser parser = new JsonParser();
		JsonObject element = parser.parse(configJSON).getAsJsonObject();
		JsonObject newOrg = parser.parse(newOrgJson).getAsJsonObject();
		
		//	Add new organisation to configuration JSON
		element.getAsJsonObject("channel_group")
		.getAsJsonObject("groups")
		.getAsJsonObject("Application")
		.getAsJsonObject("groups")
		.add(msp, newOrg);
		
		return element.toString();
	}
	
	/**
	 * When creating the channel for the first time,
	 * we need to change the policy for adding new organizations to just our org,
	 * so the channel would be easier to manage.
	 * @return
	 */
	public String changeOrganizationPolicyToAdminOrgOnly(String mspid) {

		logger.info("Changing org policy to admin org only");
		
		try {
			
			//	Read new policy from file
			String orgPolicyConfig = FileUtil.readFileAsString(orgPolicyPath);

            orgPolicyConfig = orgPolicyConfig.replace("{AdminOrgMSP}", mspid);
			
			//	Parse config and new policy to JSON
			JsonParser parser = new JsonParser();
			JsonObject element = parser.parse(configJSON).getAsJsonObject();
			JsonObject newPolicy = parser.parse(orgPolicyConfig).getAsJsonObject();
			
			//	First remove old policy
			removeOldOrgPolicy(element);
			
			//	And after that add new policy
			addNewOrgPolicy(element, newPolicy);
			
			return element.toString();
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Remove old  organization policy from JSON.
	 * 
	 * Traverses channelconfig and removes Admins-group from Application-policies.
	 * 
	 * @param config
	 * @return
	 */
	private void removeOldOrgPolicy(JsonObject config) {

		logger.info("Removing old org policy");
		
		config.getAsJsonObject("channel_group")
		.getAsJsonObject("groups")
		.getAsJsonObject("Application")
		.getAsJsonObject("policies")
		.remove("Admins");
	}
	
	/**
	 * Add new organization add-policy to JSON.
	 */
	private void addNewOrgPolicy(JsonObject config, JsonObject newPolicy) {

		logger.info("Adding new org policy");

		config.getAsJsonObject("channel_group")
		.getAsJsonObject("groups")
		.getAsJsonObject("Application")
		.getAsJsonObject("policies")
		.add("Admins", newPolicy);
		
	}
	
	/**
	 * Traverse through channel configuration using JsonParser from Google GSON
	 * and remove organisation from list using orgMSP-parameter.
	 * @param org
	 * @return
	 */
	public String removeOrganisation(String org) {

		logger.info("Removing organisation");
		
		JsonParser parser = new JsonParser();
		JsonObject element = parser.parse(configJSON).getAsJsonObject();
		
		element.getAsJsonObject("channel_group")
		.getAsJsonObject("groups")
		.getAsJsonObject("Application")
		.getAsJsonObject("groups")
		.remove(org);
		
		return element.toString();
	}
}
