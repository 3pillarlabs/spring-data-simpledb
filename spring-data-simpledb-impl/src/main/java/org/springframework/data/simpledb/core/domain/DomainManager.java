package org.springframework.data.simpledb.core.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.exception.SimpleDbExceptionTranslator;
import org.springframework.util.Assert;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;

public final class DomainManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainManager.class);

	private static DomainManager instance;
	
	private final Set<String> managedDomains = new HashSet<String>();

	private DomainManager() {
	}

	public static synchronized DomainManager getInstance() {
		if(instance == null) {
			instance = new DomainManager();
		}

		return instance;
	}

	/**
	 * Creates a domain, based on the Domain Policy; The default is UPDATE(if it does not exist create it)
	 *
	 * @return true if the domain was successfuly managed, false if the domain has been managed before
	 */
	public boolean manageDomain(final String domainName, final DomainManagementPolicy policy, final AmazonSimpleDB sdb) {
		Assert.notNull(sdb);
		
		if(! managedDomains.contains(domainName)) {
			try {
				if(policy == DomainManagementPolicy.UPDATE || policy == null) {
					createDomain(domainName, sdb);
				} else if(policy == DomainManagementPolicy.DROP_CREATE) {
					dropDomain(domainName, sdb);
					createDomain(domainName, sdb);
				}
				
				managedDomains.add(domainName);
				
				return true;
			} catch(AmazonClientException e) {
				throw SimpleDbExceptionTranslator.translateAmazonClientException(e);
			}
		} else {
			LOGGER.debug("Domain has been managed before: {}", domainName);
		}
		
		return false;
	}

	/**
	 * Running the delete-domain command over & over again on the same domain, or if the domain does NOT exist will NOT
	 * result in a Amazon Exception
	 * 
	 * @param domainName
	 */
	public void dropDomain(final String domainName, final AmazonSimpleDB sdb) {
		try {
			LOGGER.debug("Dropping domain: {}", domainName);
			DeleteDomainRequest request = new DeleteDomainRequest(domainName);
			sdb.deleteDomain(request);
			LOGGER.debug("Dropped domain: {}", domainName);
		} catch(AmazonClientException amazonException) {
			throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
		}
	}

	private void createDomain(final String domainName, final AmazonSimpleDB sdb) {
		try {
			LOGGER.debug("Creating domain: {}", domainName);
			CreateDomainRequest request = new CreateDomainRequest(domainName);
			sdb.createDomain(request);
			LOGGER.debug("Created domain: {}", domainName);
		} catch(AmazonClientException amazonException) {
			throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
		}
	}

	public boolean exists(final String domainName, final AmazonSimpleDB sdb) {
		try {
			ListDomainsResult listDomainsResult = sdb.listDomains(new ListDomainsRequest());
			List<String> domainNames = listDomainsResult.getDomainNames();
			return domainNames.contains(domainName);
		} catch(AmazonClientException amazonException) {
			throw SimpleDbExceptionTranslator.translateAmazonClientException(amazonException);
		}
	}

}
