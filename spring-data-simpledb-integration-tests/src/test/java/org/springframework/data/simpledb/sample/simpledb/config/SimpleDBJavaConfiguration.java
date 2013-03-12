package org.springframework.data.simpledb.sample.simpledb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.simpledb.config.AWSCredentials;
import org.springframework.data.simpledb.config.AbstractSimpleDBConfiguration;
import org.springframework.data.simpledb.core.SimpleDb;
import org.springframework.data.simpledb.repository.config.EnableSimpleDBRepositories;
import org.springframework.data.simpledb.repository.config.SimpleDbConfigParser;

@EnableSimpleDBRepositories(basePackages = "org.springframework.data.simpledb.sample.simpledb.repository")
@Configuration
public class SimpleDBJavaConfiguration extends AbstractSimpleDBConfiguration {

	@Override
	public AWSCredentials getAWSCredentials() {
		return new AWSCredentials("AKIAIVX775TRPPSZTEMQ", "Nzy6w0iq8JI+DHgdiPPiuqixiMoWQmPhWFgQzOZY");
	}

	@Override
	public void setExtraProperties(SimpleDb simpleDb) {
		simpleDb.setConsistentRead(true);
		simpleDb.setDomainPrefix(SimpleDbConfigParser.readHostname() + "testDB");
	}
}
