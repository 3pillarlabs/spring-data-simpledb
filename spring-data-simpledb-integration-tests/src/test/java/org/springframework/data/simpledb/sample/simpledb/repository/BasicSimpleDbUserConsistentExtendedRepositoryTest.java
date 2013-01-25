package org.springframework.data.simpledb.sample.simpledb.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-simpledb-repository-context.xml")
//@Ignore(value = "work in progress")
public class BasicSimpleDbUserConsistentExtendedRepositoryTest {

    @Autowired
    SimpleDbUserRepositoryConsistent repository;


    @After
    public void tearDown() {
        repository.deleteAll(true);
    }

    @Test
    public void consistent_find_should_return_updated_item_with_no_delay() {
        String itemName = "FirstItem";

        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user, true);
        assertNotNull(repository.findOne(itemName, true));

        repository.delete(itemName, true);
        assertNull(repository.findOne(itemName, true));
    }
}
