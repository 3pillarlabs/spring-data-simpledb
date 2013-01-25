package org.springframework.data.simpledb.sample.simpledb.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
//@Ignore(value = "work in progress")
public class BasicSimpleDbUserConsistentRepositoryTest {

    @Autowired
    BasicSimpleDbUserRepository repository;


    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void consistent_find_should_return_updated_item_with_no_delay() {
        String itemName = "FirstItem";

        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user);
        
        SimpleDbUser findOneResult = repository.findOne(itemName);
		assertNotNull(findOneResult);

        repository.delete(itemName);
        assertNull(repository.findOne(itemName));
    }

    @Test
    public void consistent_count_should_return_total_number_of_item_with_no_delay() {
        String itemName = "FirstItem";

        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user);
        assertEquals(1,repository.count());

        repository.delete(itemName);
        assertEquals(0,repository.count());
    }

}
