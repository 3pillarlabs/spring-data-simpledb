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
    public void save_should_persist_single_item() {
        String itemName = "FirstItem";

        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        //save returns argument, does not fetch from simpledb!!!
        repository.save(user);

        incrementalWaitFindOne(user.getItemName());
        SimpleDbUser foundUser = repository.findOne(user.getItemName());

        assertEquals(user.getItemName(), foundUser.getItemName());
        assertEquals(user.getAtts(), foundUser.getAtts());
    }

    @Test
    public void save_should_persist_item_list() {
        List<SimpleDbUser> list = SimpleDbUserBuilder.createListOfItems(3);

        repository.save(list);
        incrementalWaitCount(list.size());

        assertEquals(list.size(), repository.count());
    }

    @Test
    public void save_should_create_new_item_for_modified_item_name() {
        String itemName = "FirstItem";
        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user);

        itemName = "SecondItem";
        user.setItemName(itemName);
        repository.save(user);

        incrementalWaitFindOne("SecondItem");
        SimpleDbUser foundUser = repository.findOne("SecondItem");

        assertNotNull(foundUser);
        assertEquals(user.getAtts(), foundUser.getAtts());

        //initial user is still present
        incrementalWaitFindOne("FirstItem");
        foundUser = repository.findOne("FirstItem");
        assertNotNull(foundUser);
        assertEquals(user.getAtts(), foundUser.getAtts());
    }

    @Test
    public void test_save_should_persist_added_attributes() {
        String itemName = "FirstItem";

        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user);


        incrementalWaitFindOne(itemName);
        user = repository.findOne(itemName);

        user.getAtts().put("extraAttribute", "extraAttributeValue");
        repository.save(user);

        incrementalFindOneWithAttributesCount(itemName, user.getAtts().size());
        SimpleDbUser foundUser = repository.findOne(itemName);

        assertEquals("extraAttributeValue", foundUser.getAtts().get("extraAttribute"));
    }

    @Test
    public void delete_should_remove_item() {
        String itemName = "FirstItem";
        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        user = repository.save(user);

        incrementalWaitFindOne(itemName);

        repository.delete(user);

        incrementalWaitForDeletion(itemName);

        user = repository.findOne(itemName);
        assertNull(user);
    }

    @Test
    public void delete_should_remove_item_by_itemName() {
        String itemName = "FirstItem";
        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user);
        incrementalWaitFindOne(itemName);

        repository.delete(itemName);
        incrementalWaitForDeletion(itemName);

        user = repository.findOne(itemName);
        assertNull(user);
    }

    @Test
    public void delete_should_remove_list_of_items() {
        List<SimpleDbUser> list = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(list);
        incrementalWaitCount(3);

        repository.delete(list);
        incrementalWaitForDeletion(list.get(0).getItemName());
        incrementalWaitForDeletion(list.get(1).getItemName());
        incrementalWaitForDeletion(list.get(2).getItemName());

        assertEquals(0, repository.count());
    }

    @Test
    public void findOne_should_return_one_item() {
        String itemName = "FirstItem";
        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user);


        incrementalWaitFindOne(itemName);
        SimpleDbUser foundUser = repository.findOne(itemName);

        assertNotNull(foundUser);
        assertEquals(user.getItemName(), foundUser.getItemName());
        assertEquals(user.getAtts(), foundUser.getAtts());
    }

    @Test
    public void findAll_should_return_all_items() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        incrementalWaitFindOne(testUsers.get(2).getItemName());

        Iterable<SimpleDbUser> users = repository.findAll();

        assertNotNull(users);
        assertEquals(testUsers.size(), count(users));
    }

    @Test
    public void findAll_with_iterable_should_return_a_list_of_items() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);


        SimpleDbUser first = testUsers.get(0);
        Iterable<String> ids = Arrays.asList(new String[]{first.getItemName()});
        Iterable<SimpleDbUser> foundUsers = repository.findAll(ids);

        assertNotNull(foundUsers);
        assertEquals(first.getItemName(), foundUsers.iterator().next().getItemName());
    }

    @Test
    public void exists_should_return_true_for_existing_items() {
        String itemName = "FirstItem";
        SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
        repository.save(user);
        incrementalWaitFindOne(itemName);

        assertTrue(repository.exists(user.getItemName()));
    }

    private int count(Iterable<SimpleDbUser> users) {
        Iterator<SimpleDbUser> iterator = users.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            iterator.next();
        }
        return count;
    }

    private void incrementalWaitForDeletion(final String itemName) {
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        new IncrementalWait<SimpleDbUser>() {
            @Override
            public SimpleDbUser execute() {
                return repository.findOne(itemName);
            }
        }.untilResponseNull();
    }

    private void incrementalWaitFindOne(final String itemName) {
        new IncrementalWait<SimpleDbUser>() {
            @Override
            public SimpleDbUser execute() {
                return repository.findOne(itemName);
            }
        }.untilResponseNotNull();
    }

    private void incrementalFindOneWithAttributesCount(final String itemName, final int attributesCount) {
        new IncrementalWait<SimpleDbUser>() {
            @Override
            public SimpleDbUser execute() {
                return repository.findOne(itemName);
            }

            @Override
            public boolean condition(SimpleDbUser user) {
                return user.getAtts() != null && user.getAtts().size() != attributesCount;
            }
        }.untilResponseSatisfiesCondition();
    }

    private void incrementalWaitCount(final int expectedCount) {
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new IncrementalWait<SimpleDbUser>() {
            @Override
            public boolean condition() {
                return repository.count() == expectedCount;
            }
        }.untilCondition();
    }
}
