package org.springframework.data.simpledb.core.domain;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class DomainManagerTest {

    @Test
    public void manageDomains_with_DROP_CREATE_should_create_new_domain(){
        DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), "DROP_CREATE");
        manager.manageDomain("test_domain");

        assertTrue(manager.exists("test_domain"));
    }


    @Test
    public void manageDomains_with_NONE_should_NOT_create_domain(){
        DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), "NONE");
        manager.manageDomain("sample");

        assertFalse(manager.exists("sample"));
    }

    @Test
    public void manageDomains_with_UPDATE_should_create_domain_if_not_existing(){
        DomainManager manager = new DomainManager(AmazonSimpleDBClientFactory.getTestClient(), "UPDATE");
        manager.manageDomain("sample_update");

        assertTrue(manager.exists("sample_update"));
    }
}
