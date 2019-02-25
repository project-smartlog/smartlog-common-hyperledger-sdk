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
