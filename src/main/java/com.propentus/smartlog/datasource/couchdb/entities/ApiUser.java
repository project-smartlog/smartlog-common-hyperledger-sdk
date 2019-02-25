package com.propentus.smartlog.datasource.couchdb.entities;

import org.ektorp.support.CouchDbDocument;

/**
 * ApiUser model for CouchDB. ApiUser is virtual model, which acts behalf of peer in cloud installation.
 */
public class ApiUser extends CouchDbDocument {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    private String organisation;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    private String domainName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public String getSha1() { return sha1; }

    public void setSha1(String sha1) { this.sha1 = sha1; }

    private String sha1;


}
