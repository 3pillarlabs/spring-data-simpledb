package org.springframework.data.simpledb.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.data.simpledb.annotation.DomainPrefix;
import org.springframework.data.simpledb.util.StringUtil;

public class SimpleDbDomain {

	private String domainPrefix;
	private boolean dev;
	private String devDomainPrefix;

	public SimpleDbDomain() {
		
	}
	
	public SimpleDbDomain(final String domainPrefix, final boolean dev) {
		this.domainPrefix = domainPrefix;
		this.dev = dev;
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
			if(devDomainPrefix == null) {
				devDomainPrefix = readHostname();
			}
			return devDomainPrefix;
		}

		DomainPrefix annotatedDomainPrefix = (DomainPrefix) clazz.getAnnotation(DomainPrefix.class);
		if(annotatedDomainPrefix != null) {
			return annotatedDomainPrefix.value();
		}

		return this.domainPrefix;
	}
	
	public String readHostname() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return "dev_" + address.getHostName().replaceAll("\\W+", "_");
		} catch(UnknownHostException e) {
			throw new IllegalArgumentException("Could not read host name", e);
		}
	}
}