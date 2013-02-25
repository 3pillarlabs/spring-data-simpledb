package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
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
        assertEquals(testUsers.size(), result);
    }

    @Test
    public void customSelectOneUser_should_return_one_user() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        SimpleDbUser result = repository.customSelectOneUser();
        assertNotNull(result);
        assertEquals(testUsers.get(0),result);
    }

    @Test
    public void partialPrimitiveFieldSelect_should_return_a_single_primitive_field() {
        SimpleDbUser entity = SimpleDbUserBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(entity);

        float result = repository.partialPrimitiveFieldSelect();
        assertNotNull(result);
        assertThat(result, is(entity.getPrimitiveField()));
    }
}
