package org.springframework.data.simpledb.core;

public final class SimpleDbConfig {

    private String accessID;
    private String secretKey;
    private String domainManagementPolicy;
    private String consistentRead;
    private String domainPrefix;

    private static SimpleDbConfig instance;



    public static SimpleDbConfig getInstance(){
        if(instance == null){
            instance = new SimpleDbConfig();
        }

        return instance;
    }


    private SimpleDbConfig(){
        //Single instance
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

    public boolean isConsistentRead() {
        return "true".equalsIgnoreCase(consistentRead);
    }


    public void setAccessID(String accessID) {
        this.accessID = accessID;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setDomainManagementPolicy(String domainManagementPolicy) {
        this.domainManagementPolicy = domainManagementPolicy;
    }

    public void setConsistentRead(String consistentRead) {
        this.consistentRead = consistentRead;
    }

    public String getDomainPrefix() {
        return domainPrefix;
    }

    public void setDomainPrefix(String domainPrefix) {
        this.domainPrefix = domainPrefix;
    }
}
