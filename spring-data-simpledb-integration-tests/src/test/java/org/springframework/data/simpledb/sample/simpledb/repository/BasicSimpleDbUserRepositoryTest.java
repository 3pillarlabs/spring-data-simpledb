package org.springframework.data.simpledb.sample.simpledb.repository;

import java.util.ArrayList;
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

    private Map<String, String> sampleAttributeMap = new LinkedHashMap<>();

    @Autowired
    BasicSimpleDbUserRepository repository;
    SimpleDbUser user;

    @Before
    public void setUp() {
        sampleAttributeMap.put("FirstAttribute", "firstAttribute");
        sampleAttributeMap.put("SecondAttribute", "secondAttribute");
        sampleAttributeMap.put("ThirdAttribute", "thirdAttribute");
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void save_should_persist_single_item(){
        String itemName = "FirstItem";

        SimpleDbUser user = createUser(itemName, sampleAttributeMap);
        user = repository.save(user);

        assertNotNull(user);
        assertEquals(itemName, user.getItemName());
        assertEquals(sampleAttributeMap, user.getAtts());
    }

    //@Test
    public void save_should_persist_item_list(){
        List<SimpleDbUser> list = new ArrayList<>();

        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName, sampleAttributeMap);
        list.add(user);

        itemName = "SecondItem";
        SimpleDbUser secondUser = createUser(itemName, sampleAttributeMap);
        list.add(secondUser);

        itemName = "ThirdItem";
        SimpleDbUser thirdUser = createUser(itemName, sampleAttributeMap);
        list.add(thirdUser);

        repository.save(list);
        assertEquals(3, repository.count());
    }

    @Test
    public void save_should_create_new_item_for_modified_item_name(){
        String itemName = "FirstItem";

        SimpleDbUser user = createUser(itemName, sampleAttributeMap);
        user = repository.save(user);

        itemName = "SecondItem";
        user.setItemName(itemName);
        user = repository.save(user);

        assertNotNull(user);
        assertEquals(itemName, user.getItemName());
        assertEquals(sampleAttributeMap, user.getAtts());

        //initial user is still present
        user = repository.findOne("FirstItem");
        assertNotNull(user);
        assertEquals("FirstItem", user.getItemName());
        assertEquals(sampleAttributeMap, user.getAtts());
    }

    @Test
    public void save_should_persist_new_attributes_for_item(){
        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName, sampleAttributeMap);
        user = repository.save(user);

        Map<String, String> changedAttributeMap = user.getAtts();
        changedAttributeMap.put("FirstAttribute", "firstAttributeChanged");
        user.setAtts(changedAttributeMap);

        user = repository.save(user);

        assertNotNull(user);
        assertEquals(itemName, user.getItemName());
        assertEquals(sampleAttributeMap, user.getAtts());
    }

    @Test
    public void delete_should_remove_item(){
        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName, sampleAttributeMap);
        user = repository.save(user);

        repository.delete(user);

        user = repository.findOne(itemName);
        assertNull(user);
    }

    @Test
    public void delete_should_remove_item_by_itemName(){
        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName, sampleAttributeMap);
        repository.save(user);

        repository.delete(itemName);

        user = repository.findOne(itemName);
        assertNull(user);
    }

    @Test
    public void delete_should_remove_list_of_items(){
        List<SimpleDbUser> list = createListOfItemes();

        repository.delete(list);
        assertEquals(0,repository.count());
    }

    @Test
    public void findOne_should_return_one_item(){
        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName, sampleAttributeMap);
        repository.save(user);

        user = repository.findOne(itemName);

        assertNotNull(user);
        assertEquals("FirstItem", user.getItemName());
        assertEquals(sampleAttributeMap, user.getAtts());
    }


    @Test
    public void findAll_should_return_all_items(){
        createListOfItemes();

        Iterable<SimpleDbUser> users = repository.findAll();

        assertNotNull(users);
        assertEquals(3, count(users));
    }

    @Test
    public void findAll_should_return_a_list_of_items(){
        createListOfItemes();

        Iterable<SimpleDbUser> users = repository.findAll();

        assertNotNull(users);
        assertEquals(3, count(users));
    }

    private int count(Iterable<SimpleDbUser> users){
        Iterator<SimpleDbUser> iterator = users.iterator();
        int count = 0;
        while(iterator.hasNext()){
            count++;
            iterator.next();
        }
        return count;
    }

    private SimpleDbUser findUser(String itemName){
        Iterable<SimpleDbUser> allItems = repository.findAll();
        //Iterator<SimpleDbUser> iterator = allItems.iterator();
        for(Iterator<SimpleDbUser> iterator = allItems.iterator();iterator.hasNext();){
            SimpleDbUser resultUser = iterator.next();
            if (resultUser.getItemName().equals(itemName)){
                return resultUser;
            }
        }

        return null;
    }

    private SimpleDbUser createUser(String itemName, Map<String, String> atts){
        user = new SimpleDbUser();
        user.setItemName(itemName);
        user.setAtts(atts);
        return user;
    }

    private List<SimpleDbUser> createListOfItemes(){
        List<SimpleDbUser> list = new ArrayList<>();

        String itemName = "FirstItem";
        SimpleDbUser user = createUser(itemName, sampleAttributeMap);
        list.add(user);
        itemName = "SecondItem";
        SimpleDbUser secondUser = createUser(itemName, sampleAttributeMap);
        list.add(secondUser);
        itemName = "ThirdItem";
        SimpleDbUser thirdUser = createUser(itemName, sampleAttributeMap);
        list.add(thirdUser);

        repository.save(list);
        return list;
    }

}
