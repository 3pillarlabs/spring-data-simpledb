package org.springframework.data.simpledb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.simpledb.config.AWSCredentials;
import org.springframework.data.simpledb.config.AbstractSimpleDBConfiguration;

@Configuration
public class SampleConfiguration extends AbstractSimpleDBConfiguration {

    @Override
    public AWSCredentials getAWSCredentials() {
        return new AWSCredentials("sampleAccessId", "sampleSecretKey");
    }
}
