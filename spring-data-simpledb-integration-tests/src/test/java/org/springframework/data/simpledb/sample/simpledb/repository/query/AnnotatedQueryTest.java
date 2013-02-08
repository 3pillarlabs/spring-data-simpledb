package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class AnnotatedQueryTest {

    @Autowired
    AnnotatedQueryRepository repository;

    @Test
    public void customSelectAll_should_return_the_list_of_users() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<SimpleDbUser> result = repository.customSelectAll();
        assertNotNull(result);
        assertEquals(testUsers,result);
    }

    //TODO select with parameters
}
