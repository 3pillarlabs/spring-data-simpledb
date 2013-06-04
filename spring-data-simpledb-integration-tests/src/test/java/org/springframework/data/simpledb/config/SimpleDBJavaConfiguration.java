package org.springframework.data.simpledb.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.simpledb.core.SimpleDb;
import org.springframework.data.simpledb.repository.config.EnableSimpleDBRepositories;

@EnableSimpleDBRepositories(basePackages = "org.springframework.data.simpledb.repository")
@Configuration
public class SimpleDBJavaConfiguration extends AbstractSimpleDBConfiguration {

	@Override
	public AWSCredentials getAWSCredentials() {
		Properties keys = new Properties();
		try {
			keys.load(this.getClass().getResourceAsStream(
					"/aws-keys.properties"));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return new AWSCredentials(keys.getProperty("accessID"),
				keys.getProperty("secretKey"));
	}

	@Override
	public void setExtraProperties(SimpleDb simpleDb) {
		simpleDb.setConsistentRead(true);
		simpleDb.setDomainPrefix(System.getProperty("user.name") + "SimpleDB");
	}
}
