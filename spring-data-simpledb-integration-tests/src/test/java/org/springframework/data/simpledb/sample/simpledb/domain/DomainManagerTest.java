package org.springframework.data.simpledb.sample.simpledb.domain;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.simpledb.core.domain.DomainManager;

public class DomainManagerTest {

	@Test
	public void manageDomains_with_DROP_CREATE_should_create_new_domain() {
		DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), "DROP_CREATE");
		manager.manageDomain("test_domain");

		assertTrue(manager.exists("test_domain"));

		// cleanup
		manager.dropDomain("test_domain");
	}

	@Test
	public void manageDomains_with_NONE_should_NOT_create_domain() {
		DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), "NONE");
		manager.manageDomain("sample");

		assertFalse(manager.exists("sample"));

        manager.dropDomain("sample");
	}

	@Test
	public void manageDomains_with_UPDATE_should_create_domain_if_not_existing() {
		DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), "UPDATE");
		manager.manageDomain("sample_update");

		assertTrue(manager.exists("sample_update"));

		// cleanup
		manager.dropDomain("sample_update");
	}

	@Test
	public void domain_management_policies_should_be_read_case_insensitive() {
		DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), "drop_create");
		manager.manageDomain("test_domain");

		assertTrue(manager.exists("test_domain"));

		// cleanup
		manager.dropDomain("test_domain");
	}

	@Test
	public void manageDomains_with_UPDATE_should_use_default_UPDATE_policy() {
		DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), null);
		manager.manageDomain("test_domain_update");

		assertTrue(manager.exists("test_domain_update"));

        manager.dropDomain("test_domain_update");
	}

	@Test(expected = IllegalArgumentException.class)
	public void manageDomains_with_WRONG_policy_should_throw_exception() {
		DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), "wrong");
		manager.manageDomain("test_domain_none");

		assertFalse(manager.exists("test_domain_none"));

        manager.dropDomain("test_domain_none");
	}

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void manageDomain_should_throw_AmazonClientException_translated_to_spring_dao_core_exception() {
        DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), null);
        manager.dropDomain(null);
    }
}
