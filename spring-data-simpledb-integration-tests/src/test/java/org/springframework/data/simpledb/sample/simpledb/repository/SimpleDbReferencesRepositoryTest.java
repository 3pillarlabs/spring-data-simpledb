package org.springframework.data.simpledb.sample.simpledb.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-repository-context.xml")
public class SimpleDbReferencesRepositoryTest {

    @Autowired
    private SimpleDbReferencesRepository referencesRepository;

    @After
    public void tearDown() {
        referencesRepository.deleteAll();
    }


    @Test
    public void manageDomains_should_create_three_domains() {
    }
}
