package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
@Ignore
public class AnnotatedQueryWithPartialFieldsTest {

    @Autowired
    AnnotatedQueryWithPartialFieldsReqpository repository;

    @Test
    public void partialPrimitiveFieldSelect_should_return_a_single_primitive_field() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        float result = repository.partialPrimitiveFieldSelect("Item_0");
        assertNotNull(result);
        //TODO assertEquals(0.01, result, 0.001);
    }

    @Test
    public void partialPrimitiveListSelect_should_return_a_list_of_primitives() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<Float> result = repository.partialPrimitiveListSelect();
        assertNotNull(result);
        //TODO assertEquals(0.01, result, 0.001);
    }

    @Test
    public void partialCoreFieldSelect_should_return_a_core_object_field() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        String result = repository.partialCoreFieldSelect();
        assertNotNull(result);
        //TODO assertEquals("tes_string$", result);
    }

    @Test
    public void partialCoreListSelect_should_return_a_list_of_core_object_fields() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<String> result = repository.partialCoreListSelect();
        assertNotNull(result);
        //TODO assertEquals("tes_string$", result);
    }
}
