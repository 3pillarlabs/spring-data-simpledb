package org.springframework.data.simpledb.sample.simpledb.repository.query;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class AnnotatedQueryTest {

    List<SimpleDbUser> testUsers;

    @Autowired
    AnnotatedQueryRepository repository;

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void customSelectAllWrongReturnType_should_fail_wrong_returned_collection_generic_type() {
        repository.customSelectAllWrongReturnType();
    }


    @Test
    public void customSelectWithNamedParamsQuery_should_return_a_list_a_list_of() {
        List<SimpleDbUser> entities = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(entities);

        List<SimpleDbUser> result = repository.customSelectWithNamedParamsQuery(String.valueOf(0.01f), String.valueOf("Item_1"));
        assertNotNull(result);
    }

    @Test
    public void customSelectWithIndexedParams_should_return_a_list_of() {
        List<SimpleDbUser> entities = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(entities);

        List<SimpleDbUser> result = repository.customSelectWithIndexedParams(String.valueOf("tes_string$"), String.valueOf(0.01f));
        assertNotNull(result);
    }

    @Test
    public void partialPrimitiveSetSelect_should_return_a_set_of_primitives() {
        List<SimpleDbUser> entities = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(entities);

        Set<Float> result = repository.primitiveFieldSelect();
        assertNotNull(result);
        for(SimpleDbUser entity : entities){
            assertTrue(result.contains(entity.getPrimitiveField()));
        }
    }

    @Test
    public void partialObjectListSelect_should_return_a_single_object_field() {
        SimpleDbUser entity = SimpleDbUserBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(entity);

        List<JSONCompatibleClass> result = repository.partialObjectListSelect();
        assertNotNull(result);
        assertEquals(entity.getObjectList(), result);
    }


    @Test
    public void partialListOfListField_should_return_a_list_of_core_object_fields() {
        SimpleDbUser entity = SimpleDbUserBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(entity);

        List<List<Object>> result = repository.selectCoreFields();
        assertEquals(1, result.size()); //one row

        //one column
        List<Object> columns = result.get(0);
        assertEquals(1, columns.size()); //one row
    }


    @Test
    public void custom_select_with_where_clause_should_work() {
        SimpleDbUser entity = SimpleDbUserBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(entity);

        List<SimpleDbUser> result = repository.customSelectWithWhereClause();
        assertEquals(1, result.size()); //one row

        //one column
        SimpleDbUser simpelDbUser = result.get(0);
        assertEquals("Item_0", simpelDbUser.getItemName());
    }
}
