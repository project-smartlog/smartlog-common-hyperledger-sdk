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
