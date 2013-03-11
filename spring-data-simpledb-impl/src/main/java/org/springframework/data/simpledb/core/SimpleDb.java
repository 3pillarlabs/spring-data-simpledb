package org.springframework.data.simpledb.core;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.simpledb.annotation.DomainPrefix;
import org.springframework.data.simpledb.core.domain.DomainManagementPolicy;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;

/**
 * A configuration class to create and instance of {@link AmazonSimpleDB} from user credentials
 * and to hold few extra configuration options: {@link DomainManagementPolicy}, 
 * {@link DomainPrefix}, consistentRead and dev
 */
public class SimpleDb implements InitializingBean {

	private AmazonSimpleDB simpleDbClient;
	
	private String accessID;
	private String secretKey;
	
	private DomainManagementPolicy domainManagementPolicy = DomainManagementPolicy.UPDATE;
	private String domainPrefix;
	private boolean consistentRead = false;
	private boolean dev = false;
	
	public AmazonSimpleDB getSimpleDbClient() {
		return simpleDbClient;
	}
	
	public void setAccessID(String accessID) {
		this.accessID = accessID;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	public DomainManagementPolicy getDomainManagementPolicy() {
		return domainManagementPolicy;
	}
	public void setDomainManagementPolicy(DomainManagementPolicy domainManagementPolicy) {
		this.domainManagementPolicy = domainManagementPolicy;
	}

	public String getDomainPrefix() {
		return domainPrefix;
	}
	public void setDomainPrefix(String domainPrefix) {
		this.domainPrefix = domainPrefix;
	}
	
	public boolean isConsistentRead() {
		return consistentRead;
	}
	public void setConsistentRead(boolean consistentRead) {
		this.consistentRead = consistentRead;
	}
	
	public boolean isDev() {
		return dev;
	}
	public void setDev(boolean dev) {
		this.dev = dev;
	}

	@Override
	public void afterPropertiesSet() {
		final AWSCredentials awsCredentials = new AWSCredentials() {

			@Override
			public String getAWSAccessKeyId() {
				return accessID;
			}

			@Override
			public String getAWSSecretKey() {
				return secretKey;
			}
		};

		this.simpleDbClient = new AmazonSimpleDBClient(awsCredentials);
	}
	
}
