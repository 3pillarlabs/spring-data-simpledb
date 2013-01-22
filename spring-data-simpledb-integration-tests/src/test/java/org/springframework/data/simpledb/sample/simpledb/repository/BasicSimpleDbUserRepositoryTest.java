package org.springframework.data.simpledb.sample.simpledb.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-simpledb-repository-context.xml")
//@Ignore(value = "work in progress")
public class BasicSimpleDbUserRepositoryTest {

    @Autowired
    BasicSimpleDbUserRepository repository;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void test_save() {
        String itemName = "FirstItem";

        SimpleDbUser user = createUser(itemName);
        user = repository.save(user);

        assertNotNull(user);
        assertEquals(itemName, user.getItemName());
        assertEquals(itemName+"val1",user.getAtts().get(itemName+"key1"));
    }

    @Test
    public void test_save_which_does_an_update() {
        String itemName = "FirstItem";
        repository.save(createUser(itemName));

        SimpleDbUser user = repository.findOne(itemName);
        user.getAtts().put(itemName+"key4", itemName+"val4");
        repository.save(user);

        user = repository.findOne(itemName);

        assertNotNull(user);
        assertEquals(itemName, user.getItemName());
        assertEquals(itemName+"val4", user.getAtts().get(itemName+"key4"));
    }

    @Test
    public void test_findOne() {
        String itemName = "FirstItem";
        repository.save(createUser(itemName));
        repository.save(createUser("SecondItem"));

        SimpleDbUser user = repository.findOne("FirstItem");
        assertNotNull(user);
        assertEquals("FirstItem", user.getItemName());
        assertEquals(itemName+"val1",user.getAtts().get(itemName+"key1"));
    }

    @Test
    public void test_save_with_iterable() {

    }

    @Test
    public void test_exists() {

    }

    @Test
    public void test_findAll() {
        List<SimpleDbUser> users = createListOfItemes();
        repository.save(users);

        Iterable<SimpleDbUser> foundUsers = repository.findAll();

        assertNotNull(foundUsers);
        assertEquals(3, count(foundUsers));
    }

    @Test
    public void test_findAll_with_iterable() {

    }

    @Test
    public void test_count() {
        List<SimpleDbUser> list = new ArrayList<>();

        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName);
        list.add(user);

        itemName = "SecondItem";
        SimpleDbUser secondUser = createUser(itemName);
        list.add(secondUser);

        itemName = "ThirdItem";
        SimpleDbUser thirdUser = createUser(itemName);
        list.add(thirdUser);

        repository.save(list);
        assertEquals(3, repository.count());
    }

    @Test
    public void test_delete_by_id() {
        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName);
        repository.save(user);

        repository.delete(itemName);

        user = repository.findOne(itemName);
        assertNull(user);
    }

    @Test
    public void test_delete_by_type() {
        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName);
        repository.save(user);

        repository.delete(user);

        user = repository.findOne(itemName);
        assertNull(user);
    }

    @Test
    public void test_delete_by_iterable() {
        List<SimpleDbUser> list = createListOfItemes();
        repository.save(list);
        repository.delete(list);
        assertEquals(0, repository.count());
    }

    @Test
    public void test_deleteAll() {

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

    private SimpleDbUser createUser(String itemName) {
        SimpleDbUser user = new SimpleDbUser();
        {
            user.setItemName(itemName);
            Map<String, String> attributes;
            user.setAtts(attributes = new HashMap<>());
            {
                attributes.put(itemName+"key1", itemName+"val1");
                attributes.put(itemName+"key2", itemName+"val2");
                attributes.put(itemName+"key3", itemName+"val3");
            }
        }
        return user;
    }

    private List<SimpleDbUser> createListOfItemes() {
        List<SimpleDbUser> list = new ArrayList<>();
        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName);
        list.add(user);
        itemName = "SecondItem";
        SimpleDbUser secondUser = createUser(itemName);
        list.add(secondUser);
        itemName = "ThirdItem";
        SimpleDbUser thirdUser = createUser(itemName);
        list.add(thirdUser);
        return list;
    }
}
