package org.springframework.data.simpledb.core.domain;

import java.util.List;

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

public class DomainManager {

	private final AmazonSimpleDB sdb;
	private final DomainManagementPolicy policy;
	private final SimpleDbExceptionTranslator exceptionTranslator;

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainManager.class);

	public DomainManager(AmazonSimpleDB sdb, String domainUpdatePolicy) {
		Assert.notNull(sdb);
		this.sdb = sdb;

		if(domainUpdatePolicy == null) {
			policy = DomainManagementPolicy.UPDATE;
			LOGGER.warn("Domain management policy not configured. Using default value UPDATE");
		} else {
			policy = DomainManagementPolicy.valueOf(domainUpdatePolicy.toUpperCase());
		}

		exceptionTranslator = SimpleDbExceptionTranslator.getTranslatorInstance();

		LOGGER.debug("Read domain management policy: {}", policy);
	}

    /**
     * Creates a domain, based on the Domain Policy; The default is UPDATE(if it does not exist create it)
     *
     * @param domainName
     */
	public void manageDomain(String domainName) {
		try {
			if(policy == DomainManagementPolicy.NONE) {
				return;
			} else if(policy == DomainManagementPolicy.UPDATE) {
				createDomain(domainName);
			} else if(policy == DomainManagementPolicy.DROP_CREATE) {
				dropDomain(domainName);
				createDomain(domainName);
			}
		} catch(AmazonClientException e) {
			throw exceptionTranslationHandler(e);
		}
	}

	/**
	 * Running the delete-domain command over & over again on the same domain, or if the domain does NOT exist will NOT
	 * result in a Amazon Exception
	 * 
	 * @param domainName
	 */
	public void dropDomain(String domainName) {
		try {
			LOGGER.debug("Dropping domain: {}", domainName);
			DeleteDomainRequest request = new DeleteDomainRequest(domainName);
			sdb.deleteDomain(request);
			LOGGER.debug("Dropped domain: {}", domainName);
		} catch(AmazonClientException amazonException) {
			throw exceptionTranslationHandler(amazonException);
		}
	}

	private void createDomain(String domainName) {
		try {
			LOGGER.debug("Creating domain: {}", domainName);
			CreateDomainRequest request = new CreateDomainRequest(domainName);
			sdb.createDomain(request);
			LOGGER.debug("Created domain: {}", domainName);
		} catch(AmazonClientException amazonException) {
			throw exceptionTranslationHandler(amazonException);
		}
	}

	public boolean exists(String domainName) {
		try {
			ListDomainsResult listDomainsResult = sdb.listDomains(new ListDomainsRequest());
			List<String> domainNames = listDomainsResult.getDomainNames();
			return domainNames.contains(domainName);
		} catch(AmazonClientException amazonException) {
			throw exceptionTranslationHandler(amazonException);
		}
	}

	private RuntimeException exceptionTranslationHandler(AmazonClientException e) {
		RuntimeException translatedException = exceptionTranslator.translateExceptionIfPossible(e);
		if(translatedException == null) {
			translatedException = e;
		}
		return translatedException;
	}
}
