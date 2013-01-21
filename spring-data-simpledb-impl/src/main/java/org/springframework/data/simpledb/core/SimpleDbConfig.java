package org.springframework.data.simpledb.core;

import org.springframework.util.Assert;

public class SimpleDbConfig {

    private String accessID;
    private String secretKey;
    private String domainManagementPolicy;

    public SimpleDbConfig(final String accessID, final String secretKey, String domainManagementPolicy){
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
