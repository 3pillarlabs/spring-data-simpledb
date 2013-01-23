package org.springframework.data.simpledb.core;

import org.springframework.util.Assert;

public final class SimpleDbConfig {

    private String accessID;
    private String secretKey;
    private String domainManagementPolicy;

    private static SimpleDbConfig instance;

    public static SimpleDbConfig createInstance(final String accessID, final String secretKey, String domainManagementPolicy){
        if(instance == null){
             instance = new SimpleDbConfig(accessID, secretKey, domainManagementPolicy);
        }
        return instance;
    }


    public static SimpleDbConfig getInstance(){
        return instance;
    }


    private SimpleDbConfig(final String accessID, final String secretKey, String domainManagementPolicy){
        Assert.notNull(accessID);
        Assert.notNull(secretKey);

        this.accessID = accessID;
        this.secretKey = secretKey;
        this.domainManagementPolicy = domainManagementPolicy;
    }


    public String getAccessID() {
        return accessID;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getDomainManagementPolicy() {
        return domainManagementPolicy;
    }
}
