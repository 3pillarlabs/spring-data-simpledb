package org.springframework.data.simpledb.core;

public class SimpleDbTemplate implements SimpleDbOperations {

    private final String accessID;
    private final String secretKey;

    public SimpleDbTemplate(String accessID, String secretKey) {
        this.accessID = accessID;
        this.secretKey = secretKey;
    }

    public String getAccessID() {
        return accessID;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
