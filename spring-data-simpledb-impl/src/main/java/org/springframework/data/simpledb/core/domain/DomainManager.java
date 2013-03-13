package org.springframework.data.simpledb.core.domain;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;

public class DomainManager {

	private final AmazonSimpleDB sdb;
	private final DomainManagementPolicy policy;

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

		LOGGER.debug("Read domain management policy: {}", policy);
	}

	public void manageDomain(String domainName) {
		if(policy == DomainManagementPolicy.NONE) {
			return;
		} else if(policy == DomainManagementPolicy.UPDATE) {
			createDomain(domainName);
		} else if(policy == DomainManagementPolicy.DROP_CREATE) {
			dropDomain(domainName);
			createDomain(domainName);
		}

	}

	public void dropDomain(String domainName) {
		LOGGER.debug("Dropping domain: {}", domainName);
		DeleteDomainRequest request = new DeleteDomainRequest(domainName);
		sdb.deleteDomain(request);
	}

	private void createDomain(String domainName) {
		LOGGER.debug("Creating domain: {}", domainName);
		CreateDomainRequest request = new CreateDomainRequest(domainName);
		sdb.createDomain(request);
	}

	public boolean exists(String domainName) {
		ListDomainsResult listDomainsResult = sdb.listDomains(new ListDomainsRequest());
		List<String> domainNames = listDomainsResult.getDomainNames();
		return domainNames.contains(domainName);
	}

}
