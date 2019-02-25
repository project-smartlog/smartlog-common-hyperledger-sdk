package com.propentus.iot.configs;

/**
 * Contains unconfigurable static Fabric connection properties
 */
public class FabricConnectionConfigurations {

    private static FabricConnectionConfigurations instance;

    private static final int INVOKE_WAITTIME = 100000;
    private static final int DEPLOY_WAITTIME = 120000;
    private static final int GOSSIP_WAITTIME = 5000;
    private static final long PROPOSAL_WAITTIME = 120000;

    public FabricConnectionConfigurations() {

    }

    public static FabricConnectionConfigurations getInstance() {
        if(instance == null) {
            instance = new FabricConnectionConfigurations();
        }
        return instance;
    }

    public int getTransactionWaitTime() {
        return INVOKE_WAITTIME;
    }

    public int getDeployWaitTime() {
        return DEPLOY_WAITTIME;
    }

    public int getGossipWaitTime() {
        return GOSSIP_WAITTIME;
    }

    public long getProposalWaitTime() {
        return PROPOSAL_WAITTIME;
    }

}
