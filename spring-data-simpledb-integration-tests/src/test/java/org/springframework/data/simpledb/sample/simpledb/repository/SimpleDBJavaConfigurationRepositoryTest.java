package org.springframework.data.simpledb.sample.simpledb.repository;


import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.config.SimpleDBJavaConfiguration;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SimpleDBJavaConfiguration.class)
public class SimpleDBJavaConfigurationRepositoryTest {

    @Autowired
    SimpleDbUserRepositoryConsistent repository;

    @After
    public void tearDown() {
        // may fail
        repository.deleteAll(true);
    }

    @Test
    public void consistent_find_should_return_updated_item_with_no_delay() {
        String itemName = "FirstItem";

        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user, true);

        SimpleDbUser findOneResult = repository.findOne(itemName, true);
        assertNotNull(findOneResult);

        repository.delete(itemName, true);
        assertNull(repository.findOne(itemName, true));
    }

}
