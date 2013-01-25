package org.springframework.data.simpledb.sample.simpledb.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-simpledb-repository-context.xml")
//@Ignore(value = "work in progress")
public class BasicSimpleDbUserRepositoryTest {

    @Autowired
    BasicSimpleDbUserRepository repository;

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void save_without_consistent_read_does_not_ensure_item_found() {
        String itemName = "FirstItem";

        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user);

        incrementalWaitFindOne(user.getItemName());
        SimpleDbUser foundUser = repository.findOne(user.getItemName());

        assertEquals(user.getItemName(), foundUser.getItemName());
        assertEquals(user.getAtts(), foundUser.getAtts());
    }



  private void incrementalWaitFindOne(final String itemName) {
        new IncrementalWait<SimpleDbUser>() {
            @Override
            public SimpleDbUser execute() {
                return repository.findOne(itemName);
            }
        }.untilResponseNotNull();
    }


}
