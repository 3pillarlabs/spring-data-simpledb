package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class AnnotatedSingleResultQueryTest {

    @Autowired
    AnnotatedSingleResultQueryRepository repository;

    @Test
    public void customLongCount_should_return_the_number_of_users_represented_as_Long() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        long result = repository.customLongCount();
        assertNotNull(result);
        System.out.println(result);
        assertEquals(testUsers.size(), result);
    }

    @Test
    public void customlongCount_should_return_the_number_of_users_represented_as_long() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        long result = repository.customlongCount();
        assertNotNull(result);
        System.out.println(result);
        assertEquals(testUsers.size(),result);
    }

    @Test
    public void customIntCount_should_fail_if_return_number_of_users__is_represented_as_int() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        try{
            int result = repository.customIntCount();
        }catch (IllegalArgumentException e){
            assertTrue(e.getMessage().contains("Method declared in repository should return type long or Long"));
            return;
        }
        fail();
    }

    @Test
    public void customSelectOneUser_should_return_one_users() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        SimpleDbUser result = repository.customSelectOneUser();
        assertNotNull(result);
        assertEquals(testUsers.get(0),result);
    }

    @Test
    public void customSelectOneUser_should_fail_if_more_users_can_be_selected() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        try{
            SimpleDbUser result = repository.customFailSelectOneUser();
        }catch (IllegalArgumentException e){
            assertTrue(e.getMessage().contains("Select statement doesn't return only one entity"));
            return;
        }
        fail();
    }


    //TODO select with parameters
}
