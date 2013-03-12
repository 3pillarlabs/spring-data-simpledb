package org.springframework.data.simpledb.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.simpledb.annotation.DomainPrefix;
import org.springframework.data.simpledb.core.domain.DomainManagementPolicy;
import org.springframework.data.simpledb.util.StringUtil;

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
    private DomainManagementPolicy policy = DomainManagementPolicy.UPDATE;
	
	private String accessID;
	private String secretKey;
	
	private String domainManagementPolicy;
	private String domainPrefix;
	private boolean consistentRead = false;
	private boolean dev = false;
	
	private String devDomainPrefix;

    public SimpleDb(){
        //for spring bean injection
    }

    public SimpleDb(String accessID, String secretKey){
        //to be used in Java configuration when bean is not read from config
        this.accessID = accessID;
        this.secretKey = secretKey;
    }
	
	public AmazonSimpleDB getSimpleDbClient() {
		return simpleDbClient;
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

    public DomainManagementPolicy getDomainManagementPolicy(){
        return policy;
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
	public final void afterPropertiesSet() {
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
		
		if(this.dev) {
			devDomainPrefix = readHostname();
		}

        if(!StringUtils.isEmpty(domainManagementPolicy)) {
            policy = DomainManagementPolicy.valueOf(domainManagementPolicy);
        }
	}
	
	/**
	 * Domain name are computed based on class names: UserJob -> user_job
	 * 
	 * @param clazz
	 * @return
	 */
	public String getDomain(Class<?> clazz) {
		StringBuilder ret = new StringBuilder();

		String computedDomainPrefix = getDomainPrefix(clazz);
		if(computedDomainPrefix != null) {
			ret.append(computedDomainPrefix);
			ret.append(".");
		}

		String camelCaseString = clazz.getSimpleName();

		ret.append(StringUtil.toLowerFirstChar(camelCaseString));

		return ret.toString();
	}
	
	private String getDomainPrefix(Class<?> clazz) {
		if(this.dev) {
			return devDomainPrefix;
		}

		DomainPrefix annotatedDomainPrefix = (DomainPrefix) clazz.getAnnotation(DomainPrefix.class);
		if(annotatedDomainPrefix != null) {
			return annotatedDomainPrefix.value();
		}

		return this.domainPrefix;
	}
	
	private String readHostname() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return "dev_" + address.getHostName().replaceAll("\\W+", "_");
		} catch(UnknownHostException e) {
			throw new IllegalArgumentException("Could not read host name", e);
		}
	}
}
