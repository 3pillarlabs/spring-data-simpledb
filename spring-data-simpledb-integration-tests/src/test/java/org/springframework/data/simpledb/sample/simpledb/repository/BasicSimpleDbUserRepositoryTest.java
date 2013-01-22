package org.springframework.data.simpledb.sample.simpledb.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-simpledb-repository-context.xml")
@Transactional
//@Ignore(value = "work in progress")
public class BasicSimpleDbUserRepositoryTest {

    @Autowired
    BasicSimpleDbUserRepository repository;
    SimpleDbUser user;

    @Before
    public void setUp() {
        user = new SimpleDbUser();
    }


    @After
    public void tearDown(){
        repository.deleteAll();
    }

    @Test
    public void save_should_do_something() {
        user.setItemName("TestItemName");
        Map<String, String> atts = new LinkedHashMap<>();
        atts.put("testAtt", "testValue");
        user.setAtts(atts);
        user = repository.save(user);
    }
    
    @Test
    public void findAll_test() {
    	final Iterable<SimpleDbUser> allItems = repository.findAll();
    	
    	Assert.notNull(allItems);
    }
    
    @Test
    public void findAllIds_test() {
    	List<String> ids = new ArrayList<>();
    	ids.add("TestItemName");
    	ids.add("2");
    	final Iterable<SimpleDbUser> allItems = repository.findAll(ids);
    	Assert.notNull(allItems);
    }

}
