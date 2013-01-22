package org.springframework.data.simpledb.sample.simpledb.repository;

import java.util.*;

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


    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void save_should_persist_single_item() {
        String itemName = "FirstItem";

        SimpleDbUser user = createUserWithSampleAttributes(itemName);
        //save returns argument, does not fetch from simpledb!!!
        repository.save(user);


        SimpleDbUser foundUser = repository.findOne(user.getItemName());

        assertEquals(user.getItemName(), foundUser.getItemName());
        assertEquals(user.getAtts(), foundUser.getAtts());
    }


    @Test
    public void save_should_persist_item_list() {
        List<SimpleDbUser> list = createListOfItems(3);

        repository.save(list);
        assertEquals(list.size(), repository.count());
    }

    @Test
    public void save_should_create_new_item_for_modified_item_name() {
        String itemName = "FirstItem";
        SimpleDbUser user = createUserWithSampleAttributes(itemName);
        repository.save(user);

        itemName = "SecondItem";
        user.setItemName(itemName);
        repository.save(user);

        SimpleDbUser foundUser = repository.findOne("SecondItem");
        assertNotNull(foundUser);
        assertEquals(user.getAtts(), foundUser.getAtts());

        //initial user is still present
        foundUser = repository.findOne("FirstItem");
        assertNotNull(foundUser);
        assertEquals(user.getAtts(), foundUser.getAtts());
    }


    @Test
    public void test_save_should_persist_added_attributes() {
        String itemName = "FirstItem";

        SimpleDbUser user = createUserWithSampleAttributes(itemName);

        user = repository.findOne(itemName);
        user.getAtts().put("extraAttribute", "extraAttributeValue");
        repository.save(user);

        SimpleDbUser foundUser = repository.findOne(itemName);

        assertEquals("extraAttributeValue", foundUser.getAtts().get("extraAttribute"));
    }


    @Test
    public void delete_should_remove_item() {
        String itemName = "FirstItem";
        SimpleDbUser user = createUserWithSampleAttributes(itemName);
        user = repository.save(user);

        repository.delete(user);

        user = repository.findOne(itemName);
        assertNull(user);
    }

    @Test
    public void delete_should_remove_item_by_itemName() {
        String itemName = "FirstItem";
        SimpleDbUser user = createUserWithSampleAttributes(itemName);
        repository.save(user);

        repository.delete(itemName);

        user = repository.findOne(itemName);
        assertNull(user);
    }

    @Test
    public void delete_should_remove_list_of_items() {
        List<SimpleDbUser> list = createListOfItems(3);
        repository.save(list);
        repository.delete(list);

        assertEquals(0, repository.count());
    }

    @Test
    public void findOne_should_return_one_item() {
        String itemName = "FirstItem";
        SimpleDbUser user = createUserWithSampleAttributes(itemName);
        repository.save(user);

        SimpleDbUser foundUser = repository.findOne(itemName);

        assertNotNull(foundUser);
        assertEquals(user.getItemName(), foundUser.getItemName());
        assertEquals(user.getAtts(), foundUser.getAtts());
    }


    @Test
    public void findAll_should_return_all_items() {
        List<SimpleDbUser> testUsers = createListOfItems(3);
        repository.save(testUsers);

        Iterable<SimpleDbUser> users = repository.findAll();

        assertNotNull(users);
        assertEquals(testUsers.size(), count(users));
    }

    @Test
    public void findAll_with_iterable_should_return_a_list_of_items() {
        List<SimpleDbUser> testUsers = createListOfItems(3);
        repository.save(testUsers);


        SimpleDbUser first = testUsers.get(0);
        Iterable<String> ids = Arrays.asList(new String[]{first.getItemName()});
        Iterable<SimpleDbUser> foundUsers = repository.findAll(ids);

        assertNotNull(foundUsers);
        assertEquals(first.getItemName(), foundUsers.iterator().next().getItemName());
    }

    @Test
    public void exists_should_return_true_for_existing_items(){
        String itemName = "FirstItem";
        SimpleDbUser user = createUserWithSampleAttributes(itemName);
        repository.save(user);

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

    private SimpleDbUser createUserWithSampleAttributes(String itemName) {

        Map<String, String> sampleAttributeMap = new LinkedHashMap<>();
        {
            sampleAttributeMap.put(itemName + "_Key1", itemName + "_Value1");
            sampleAttributeMap.put(itemName + "_Key2", itemName + "_Value2");
            sampleAttributeMap.put(itemName + "_Key3", itemName + "_Value3");
        }

        SimpleDbUser user = new SimpleDbUser();
        user.setItemName(itemName);
        user.setAtts(sampleAttributeMap);
        return user;
    }

    private List<SimpleDbUser> createListOfItems(int length) {
        List<SimpleDbUser> list = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            String itemName = "Item_" + i;
            SimpleDbUser user = createUserWithSampleAttributes(itemName);
            list.add(user);
        }
        return list;
    }

}
