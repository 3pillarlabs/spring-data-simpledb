package org.springframework.data.simpledb.core;

import org.springframework.data.simpledb.annotation.DomainPrefix;
import org.springframework.data.simpledb.util.StringUtil;

public class SimpleDbDomain {

	private String domainPrefix;

	public SimpleDbDomain() {
		
	}
	
	public SimpleDbDomain(final String domainPrefix) {
		this.domainPrefix = domainPrefix;
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
		String prefix = null;
		DomainPrefix annotatedDomainPrefix = clazz.getAnnotation(DomainPrefix.class);
		if (annotatedDomainPrefix != null) {
			prefix = annotatedDomainPrefix.value();
		} else {
			prefix = this.domainPrefix;
		}

		return prefix;
	}
	
}