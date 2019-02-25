package com.propentus.iot;

import com.propentus.common.exception.BlockchainException;
import com.propentus.iot.configs.OrganisationConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.testutils.TestConfig;
import org.hyperledger.fabric.sdkintegration.SampleOrg;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Initializes SDK with channel information so we can interact with blockchain.
 */
public class ChannelManager {

	private static final Log logger = LogFactory.getLog(ChannelManager.class);

	private TestConfig testConfig;

	public ChannelManager(OrganisationConfiguration configuration) {
		testConfig = TestConfig.getConfig(configuration);
	}

    /**
     * Join to existing channel on Hyperledger fabric.
     * @param name
     * @param client
     * @param sampleOrg
     * @return
     * @throws Exception
     */
	public Channel joinChannel(String name, HFClient client, SampleOrg sampleOrg) throws Exception {

	    logger.info("Initializing channel information");

        client.setUserContext(sampleOrg.getPeerAdmin());
        Channel newChannel = client.newChannel(name);

        for (String orderName : sampleOrg.getOrdererNames()) {
            newChannel.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                    testConfig.getOrdererProperties(orderName)));
        }

        for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);
            Peer peer = client.newPeer(peerName, peerLocation, testConfig.getPeerProperties(peerName));

            //Query the actual peer for which channels it belongs to and check it belongs to this channel
            try {
                Set<String> channels = client.queryChannels(peer);
            }
            catch (ProposalException e) {
                //Failed to join. Network issues.
                throw new BlockchainException("Client failed to join channel because of 'connection refused'. Exception:", e);
            }

            newChannel.addPeer(peer);
            sampleOrg.addPeer(peer);
        }

        for (String eventHubName : sampleOrg.getEventHubNames()) {
            EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
                    testConfig.getEventHubProperties(eventHubName));
            newChannel.addEventHub(eventHub);
        }

        newChannel.initialize();

        logger.info("Channel " + name + " initialized!");

        return newChannel;
    }
}
