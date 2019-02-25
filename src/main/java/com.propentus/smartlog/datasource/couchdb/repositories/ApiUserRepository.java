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

package com.propentus.smartlog.datasource.couchdb.repositories;

import com.propentus.smartlog.datasource.couchdb.entities.ApiUser;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * CouchDB methods for ApiUser entity.
 */
public class ApiUserRepository extends CouchDbRepositorySupport<ApiUser> {

    private static Logger logger = LoggerFactory.getLogger(ApiUserRepository.class);

    public ApiUserRepository(CouchDbConnector db) {
        super(ApiUser.class, db);
        this.initStandardDesignDocument();
    }

    @GenerateView
    public ApiUser findByAuthToken(String authToken) {
        List<ApiUser> users = queryView("by_authToken", authToken);
        if(users.isEmpty()) {
            logger.info("No ApiUser found with authToken: " + authToken);
            return null;
        }
        if(users.size() > 1) {
            logger.error("Multiple ApiUsers with same AuthToken.");
            return null;
        }
        return users.get(0);
    }

    @GenerateView
    public ApiUser findByDomainName(String domainName) {

        List<ApiUser> users = queryView("by_domainName", domainName);

        if(users.isEmpty()) {
            logger.info("No ApiUser found with domainName: " + domainName);
            return null;
        }

        if(users.size() > 1) {
            logger.error("Multiple ApiUsers with same domain name.");
            return null;
        }

        return users.get(0);
    }

    @GenerateView
    public ApiUser findBySha1(String sha1) {

        List<ApiUser> users = queryView("by_sha1", sha1);

        if(users.isEmpty()) {
            logger.info("No ApiUser found with sha1: " + sha1);
            return null;
        }

        if(users.size() > 1) {
            logger.error("Multiple ApiUsers with same sha1.");
            return null;
        }

        return users.get(0);
    }

    public List<ApiUser> findAll() {
        List<ApiUser> users = new ArrayList<ApiUser>();
        users = getAll();
        return users;
    }

    public void delete(ApiUser user) {

        super.remove(user);
    }


}
