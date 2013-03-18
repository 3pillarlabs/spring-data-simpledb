package org.springframework.data.simpledb.sample.simpledb.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.simpledb.core.domain.DomainManagementPolicy;
import org.springframework.data.simpledb.core.domain.DomainManager;
import org.springframework.data.simpledb.util.HostNameResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-repository-context.xml")
public class DomainManagerTest {

	@Test
	public void manageDomains_with_DROP_CREATE_should_create_new_domain() {
		final String domainName = getDomainName("test_domain");
		
		DomainManager manager = DomainManager.getInstance(); 
		manager.manageDomain(domainName, DomainManagementPolicy.DROP_CREATE, AmazonSimpleDBClientFactory.getTestClient());

		assertTrue(manager.exists(domainName, AmazonSimpleDBClientFactory.getTestClient()));

		// cleanup
		manager.dropDomain(domainName, AmazonSimpleDBClientFactory.getTestClient());
	}

	@Test
	public void manageDomains_with_NONE_should_NOT_create_domain() {
		final String domainName = getDomainName("sample");
		
		DomainManager manager = DomainManager.getInstance();
		manager.manageDomain(domainName, DomainManagementPolicy.NONE, AmazonSimpleDBClientFactory.getTestClient());

		assertFalse(manager.exists(domainName, AmazonSimpleDBClientFactory.getTestClient()));

        manager.dropDomain(domainName, AmazonSimpleDBClientFactory.getTestClient());
	}

	@Test
	public void manageDomains_with_UPDATE_should_create_domain_if_not_existing() {
		final String domainName = getDomainName("sample_update");
		
		DomainManager manager = DomainManager.getInstance();
		manager.manageDomain(domainName, DomainManagementPolicy.UPDATE, AmazonSimpleDBClientFactory.getTestClient());

		assertTrue(manager.exists(domainName, AmazonSimpleDBClientFactory.getTestClient()));

		// cleanup
		manager.dropDomain(domainName, AmazonSimpleDBClientFactory.getTestClient());
	}

	@Test
	public void manageDomains_with_UPDATE_should_use_default_UPDATE_policy() {
		final String domainName = getDomainName("test_domain_update");
		
		DomainManager manager = DomainManager.getInstance();
		manager.manageDomain(domainName, null, AmazonSimpleDBClientFactory.getTestClient());

		assertTrue(manager.exists(domainName, AmazonSimpleDBClientFactory.getTestClient()));

        manager.dropDomain(domainName, AmazonSimpleDBClientFactory.getTestClient());
	}
	
	@Test
	public void managing_same_domain_more_than_once_should_return_false() {
		final String domainName = getDomainName("test_domain_multiple");
		
		DomainManager manager = DomainManager.getInstance();
		boolean result = manager.manageDomain(domainName, null, AmazonSimpleDBClientFactory.getTestClient());

		assertTrue(result);
		
		result = manager.manageDomain(domainName, null, AmazonSimpleDBClientFactory.getTestClient());
		
		assertFalse(result);
	}

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void manageDomain_should_throw_AmazonClientException_translated_to_spring_dao_core_exception() {
        DomainManager manager = DomainManager.getInstance();
        manager.dropDomain(null, AmazonSimpleDBClientFactory.getTestClient());
    }
    
    private String getDomainName(final String domain) {
    	return HostNameResolver.readHostname() + "." + domain;
    }
}
