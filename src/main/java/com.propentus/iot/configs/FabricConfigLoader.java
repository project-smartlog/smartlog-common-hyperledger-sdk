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

import org.hyperledger.fabric.sdk.Channel;

import com.propentus.iot.ChannelManager;
import com.propentus.smartlog.configtxlator.ConfigTxLatorService;

/**
 * Load Fabric config JSON from channel
 * @author
 */
public class FabricConfigLoader extends AbstractConfiguration {
	
	/**
	 * Loads config block from channel and parses it to JSON using configtxlator
	 * @return
	 * @throws Exception
	 */
	public static String getConfigFile() throws Exception {
		
		byte[] configBlock = getConfigBlock();
		String config = ConfigTxLatorService.getConfigAsJson(configBlock);
		
		return config;
	}
	
	/**
	 * Reconstructs channel and gets its channelConfigurationBytes (configuration block)
	 * @return
	 * @throws Exception
	 */
	private static final byte[] getConfigBlock() throws Exception {
		
		BasicSettingInitializer bsi = new BasicSettingInitializer();

		OrganisationConfiguration orgConf = bsi.getConfigReader().getOrganisationConfiguration();
		ChannelManager channelManager = new ChannelManager(orgConf);
		Channel channel = channelManager.joinChannel(orgConf.channel, bsi.getHFClient(), bsi.getSampleOrg());
		
		return channel.getChannelConfigurationBytes();
	}
	
	
}
