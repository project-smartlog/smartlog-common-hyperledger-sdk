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

import com.propentus.iot.configs.ConfigReader;
import com.propentus.iot.configs.OrganisationConfiguration;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.testutils.TestConfig;
import org.hyperledger.fabric.sdkintegration.SampleOrg;
import org.hyperledger.fabric.sdkintegration.SampleStore;
import org.hyperledger.fabric.sdkintegration.SampleUser;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import static java.lang.String.format;

public class UserFactory {

	private Logger logger = LoggerFactory.getLogger(UserFactory.class);

	private final String SAMPLE_FILE_STORE_PATH = "/temp//IoT/sample_store.properties";
	
	private SampleStore sampleStore;
	private ConfigReader configReader;
	private TestConfig testConfig;
	
	public UserFactory(ConfigReader reader) {
		this.sampleStore = new SampleStore(new File(SAMPLE_FILE_STORE_PATH));
		this.configReader = reader;
	}
	
	public SampleUser getPeerAdmin(SampleOrg sampleOrg) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException {

		OrganisationConfiguration configuration = this.configReader.getOrganisationConfiguration();

		String sampleOrgName = sampleOrg.getName();
		String sampleOrgDomainName = sampleOrg.getDomainName();

		SampleUser peerOrgAdmin = sampleStore.getMember(sampleOrgName + "Admin", sampleOrgName, sampleOrg.getMSPID(),
				findFileSk(Paths.get(configuration.fabricEnvPath, "crypto-config/peerOrganizations/",
						sampleOrgDomainName, format("/users/Admin@%s/msp/keystore", sampleOrgDomainName)).toFile()),
				Paths.get(configuration.fabricEnvPath, "crypto-config/peerOrganizations/", sampleOrgDomainName,
						format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem", sampleOrgDomainName, sampleOrgDomainName)).toFile());

		return peerOrgAdmin;
	}
	
    File findFileSk(File directory) {

		logger.info("Finding SK files from directory: '{}'", directory);

        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }

        if (matches.length != 1) {
        	 throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }

        return matches[0];

    }
	
	public void setUsers(SampleOrg sampleOrg) throws EnrollmentException, InvalidArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
        final String orgName = sampleOrg.getName();
        final String mspid = sampleOrg.getMSPID();
        
        SampleUser admin = this.sampleStore.getMember("admin", orgName);

		admin.setMspId(mspid);
        
        sampleOrg.setAdmin(admin); // The admin of this org --
	}
}
