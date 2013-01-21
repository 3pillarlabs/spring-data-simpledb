package org.springframework.data.simpledb.sample.simpledb.repository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.jpa.domain.User;
import org.springframework.data.simpledb.sample.jpa.repository.BasicUserRepository;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void save_should_do_something() {
        user.setItemName("TestItemName");
        Map<String, String> atts = new LinkedHashMap<>();
        atts.put("testAtt", "testValue");
        user.setAtts(atts);
        user = repository.save(user);
    }

}
