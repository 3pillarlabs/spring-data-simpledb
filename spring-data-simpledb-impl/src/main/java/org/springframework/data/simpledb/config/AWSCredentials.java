package org.springframework.data.simpledb.config;

public class AWSCredentials {

    private String accessID;
    private String secretKey;


    public AWSCredentials(String accessID, String secretKey){
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
