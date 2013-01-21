package org.springframework.data.simpledb.core.domain;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class DomainManagerTest {

    public static final String TEST_AMAZON_ACCESS_KEY = "AKIAIVX775TRPPSZTEMQ";
    public static final String TEST_AMAZON_PRIVATE_KEY = "Nzy6w0iq8JI+DHgdiPPiuqixiMoWQmPhWFgQzOZY";

    @Test
    public void manageDomains_with_DROP_CREATE_should_create_new_domain(){
        DomainManager manager = new DomainManager(TEST_AMAZON_ACCESS_KEY, TEST_AMAZON_PRIVATE_KEY, "DROP_CREATE");
        manager.manageDomain("test_domain");

        assertTrue(manager.exists("test_domain"));
    }


    @Test
    public void manageDomains_with_NONE_should_NOT_create_domain(){
        DomainManager manager = new DomainManager(TEST_AMAZON_ACCESS_KEY, TEST_AMAZON_PRIVATE_KEY, "NONE");
        manager.manageDomain("sample");

        assertFalse(manager.exists("sample"));
    }

    @Test
    public void manageDomains_with_UPDATE_should_create_domain_if_not_existing(){
        DomainManager manager = new DomainManager(TEST_AMAZON_ACCESS_KEY, TEST_AMAZON_PRIVATE_KEY, "UPDATE");
        manager.manageDomain("sample_update");

        assertTrue(manager.exists("sample_update"));
    }
}
