package org.springframework.data.simpledb.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-simpledb-repository-context.xmll")
public class SimpleDBDomainOperationsTest {

    @Autowired
    private SimpleDBDomainOperations cut;

    @Test
    public void dropDomain_should_not_throw_NPE_for_annotation_parser() throws Exception {
        cut.dropDomain();
    }

    @Test
    public void testCreateDomain() throws Exception {

    }

    @Test
    public void testCreateDomainsIfNotExist() throws Exception {

    }
}
