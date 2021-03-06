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

package com.propentus.smartlog.datasource.couchdb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.propentus.common.exception.ConfigurationException;
import com.propentus.common.util.StringUtil;
import com.propentus.iot.configs.OrganisationConfiguration;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.HttpResponse;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class CouchDBProvider {

    private static final Logger logger = LoggerFactory.getLogger(CouchDBProvider.class);

    private static final String DATABASE = "ApiUsers";

    private static final String USERNAME = System.getProperty("smartlog.managementdb.username");
    private static final String PASSWORD = System.getProperty("smartlog.managementdb.password");
    private static final String URL = System.getProperty("smartlog.managementdb.url");

    public CouchDBProvider() throws ConfigurationException {
        if(!validate()) {
            throw new ConfigurationException("CouchDB password, username or url was not configured correctly!");
        }
    }

    private HttpClient createClient() throws MalformedURLException {
        HttpClient httpClient = new StdHttpClient.Builder()
                .url(URL)
                .username(USERNAME)
                .password(PASSWORD)
                .build();
        return httpClient;
    }

    public CouchDbConnector getConnector() throws MalformedURLException {

        CouchDbInstance dbInstance = new StdCouchDbInstance(createClient());
        CouchDbConnector db = new StdCouchDbConnector(DATABASE, dbInstance);

        return db;
    }

    public void addObject(Object obj) {
        try {
            CouchDbConnector connector = getConnector();
            String message = serialize(obj);
           HttpResponse response = createClient().post(URL + "/" + DATABASE + "/", message);
           System.out.println(response);

        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private String serialize(Object o) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(o);
    }

    private boolean validate()  {
        return (!StringUtil.isEmpty(USERNAME) && !StringUtil.isEmpty(PASSWORD) && !StringUtil.isEmpty(URL));
    }

}
