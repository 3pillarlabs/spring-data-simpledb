package org.springframework.data.simpledb.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.simpledb.core.SimpleDb;
import org.springframework.data.simpledb.core.domain.DomainManagementPolicy;
import org.springframework.data.simpledb.core.domain.DomainManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.simpledb.AmazonSimpleDB;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-repository-context.xml")
public class DomainManagerTest {

	@Autowired
	private ApplicationContext appContext;
	private AmazonSimpleDB sdb;
	
	@Before
	public void setup() {
		SimpleDb simpleDb = appContext.getBean(SimpleDb.class);
		sdb = simpleDb.getSimpleDbClient();
	}
	
	@Test
	public void manageDomains_with_DROP_CREATE_should_create_new_domain() {
		final String domainName = getDomainName("test_domain");
		
		DomainManager manager = DomainManager.getInstance(); 
		manager.manageDomain(domainName, DomainManagementPolicy.DROP_CREATE, sdb);

		assertTrue(manager.exists(domainName, sdb));

		// cleanup
		manager.dropDomain(domainName, sdb);
	}

	@Test
	public void manageDomains_with_NONE_should_NOT_create_domain() {
		final String domainName = getDomainName("sample");
		
		DomainManager manager = DomainManager.getInstance();
		manager.manageDomain(domainName, DomainManagementPolicy.NONE, sdb);

		assertFalse(manager.exists(domainName, sdb));

        manager.dropDomain(domainName, sdb);
	}

	@Test
	public void manageDomains_with_UPDATE_should_create_domain_if_not_existing() {
		final String domainName = getDomainName("sample_update");
		
		DomainManager manager = DomainManager.getInstance();
		manager.manageDomain(domainName, DomainManagementPolicy.UPDATE, sdb);

		assertTrue(manager.exists(domainName, sdb));

		// cleanup
		manager.dropDomain(domainName, sdb);
	}

	@Test
	public void manageDomains_with_UPDATE_should_use_default_UPDATE_policy() {
		final String domainName = getDomainName("test_domain_update");
		
		DomainManager manager = DomainManager.getInstance();
		manager.manageDomain(domainName, null, sdb);

		assertTrue(manager.exists(domainName, sdb));

        manager.dropDomain(domainName, sdb);
	}
	
	@Test
	public void managing_same_domain_more_than_once_should_return_false() {
		final String domainName = getDomainName("test_domain_multiple");
		
		DomainManager manager = DomainManager.getInstance();
		boolean result = manager.manageDomain(domainName, null, sdb);

		assertTrue(result);
		
		result = manager.manageDomain(domainName, null, sdb);
		
		assertFalse(result);
	}

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void manageDomain_should_throw_AmazonClientException_translated_to_spring_dao_core_exception() {
        DomainManager manager = DomainManager.getInstance();
        manager.dropDomain(null, sdb);
    }
    
    private String getDomainName(final String domain) {
    	return System.getProperty("user.name") + "." + domain;
    }
}
