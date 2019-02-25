package com.propentus.iot;

import com.propentus.common.exception.BlockchainException;
import com.propentus.common.exception.ConfigurationException;
import com.propentus.iot.configs.BasicSettingInitializer;
import com.propentus.iot.configs.OrganisationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.Collection;
import java.util.LinkedList;


/**
 * Should be used as main entry point for talking with Hyperledger Fabric from outside sources
 */
public class BlockchainConnector {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainConnector.class);

    private Channel currentChannel;
    private BasicSettingInitializer settingInitializer;

    public BlockchainConnector() throws ConfigurationException, BlockchainException {
        initialize();
    }

    /**
     * Initialize this Blockchain connector instance
     * @throws ConfigurationException
     */
    private void initialize() throws ConfigurationException, BlockchainException {
        //Load configs for this client
        settingInitializer = new BasicSettingInitializer();

        joinChannel();
    }

    private void joinChannel() throws BlockchainException {
        // Try to join channel when connector is created.
        try {
            OrganisationConfiguration orgConf = settingInitializer.getConfigReader().getOrganisationConfiguration();
            ChannelManager channelManager = new ChannelManager(orgConf);
            currentChannel = channelManager.joinChannel(orgConf.channel, settingInitializer.getHFClient(), settingInitializer.getSampleOrg());
            logger.debug("Joined channel '" + currentChannel.getName() + "' succesfully");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BlockchainException("BlockchainConnector threw exception when trying to join channel!", e);
        }
    }

    public User getUser() {
        return this.settingInitializer.getHFClient().getUserContext();
    }

    /**
     * Get current channel where this connector belongs to. This information is not persistent and BlockchainConnector will try to join channel on startup always!
     * @return Current channel this Blockchain connector is participant of.
     */
    public Channel getChannel() {
        if(currentChannel == null) {
            logger.error("Connector has no channel information");
        }
        return  this.currentChannel;
    }

    /**
     * Send transaction request to Smart contract and return decoded response as String
     * @param request
     * @return
     */
    public String doTransaction(TransactionProposalRequest request) throws BlockchainException {

        Channel channel = this.getChannel();
        User user = this.getUser();

        LinkedList<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        Collection<ProposalResponse> invokePropResp = null;
        try {
            invokePropResp = this.getChannel().sendTransactionProposal(request);
        } catch (ProposalException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        } catch (InvalidArgumentException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
        }

        for (ProposalResponse response : invokePropResp) {
            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        ////////////////////////////
        // Send transaction to orderer and return value
        if (user != null) {
            channel.sendTransaction(successful, user);
            if(!successful.isEmpty()) {
                return successful.get(0).getProposalResponse().getResponse().getPayload().toStringUtf8();
            }
        }
        //successful
        //return channel.sendTransaction(successful);
        return null;
    }

    public String doQuery(QueryByChaincodeRequest request) {

        try {
            Channel channel = this.getChannel();

            Collection<ProposalResponse> responses = currentChannel.queryByChaincode(request);

            //Got response
            if(!responses.isEmpty()) {
                for (ProposalResponse response : responses) {
                    String responseValue = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    logger.debug("Received query response: " + responseValue);
                    return responseValue;
                }
            }

        }
        catch(Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.error("Query received no response!");
        return  null;
    }

    public OrganisationConfiguration getConfig() {
        return this.settingInitializer.getConfigReader().getOrganisationConfiguration();
    }

}
