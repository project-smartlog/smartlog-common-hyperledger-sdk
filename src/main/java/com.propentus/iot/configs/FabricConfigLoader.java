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
