package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbDifferentFieldTypes;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbDifferentFiledTypesBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")

public class AnnotatedQueryWithSinglePartialFieldTest {

    @Autowired
    AnnotatedQueryWithPartialFieldsRepository repository;

    @Test
    public void partialPrimitiveFieldSelect_should_return_a_single_primitive_field() {
        List<SimpleDbDifferentFieldTypes> testUsers = SimpleDbDifferentFiledTypesBuilder.createListOfItems(1);
        repository.save(testUsers);

        float result = repository.partialPrimitiveFieldSelect();
        assertNotNull(result);
        assertThat(result, is(testUsers.get(0).getPrimitiveField()));
    }

    @Test
    @Ignore
    public void partialNestedEntitySelect_should_return_a_single_object_field() {
        List<SimpleDbDifferentFieldTypes> testUsers = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(testUsers);

        JSONCompatibleClass result = repository.partialNestedEntitySelect();
        assertNotNull(result);
        assertEquals(testUsers.get(0).getJsonCompatibleClass(), result);
    }

    @Test
    @Ignore
    public void partialObjectListSelect_should_return_a_single_object_field() {
        List<SimpleDbDifferentFieldTypes> testUsers = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<JSONCompatibleClass> result = repository.partialObjectListSelect();
        assertNotNull(result);
        assertEquals(testUsers.get(0).getJsonCompatibleClass(), result);
    }

    @Test
    public void partialPrimitiveListSelect_should_return_a_list_of_primitives() {
        List<SimpleDbDifferentFieldTypes> testUsers = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<Float> result = repository.partialPrimitiveListSelect();
        assertNotNull(result);
        for(SimpleDbDifferentFieldTypes simpleUser : testUsers){
            assertTrue(result.indexOf(simpleUser.getPrimitiveField()) != -1);
        }
    }

    @Test
    public void partialCoreTypeListSelectSelect_should_return_a_list_of_primitives() {
        List<SimpleDbDifferentFieldTypes> testUsers = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<Integer> result = repository.partialCoreTypeListSelect();
        assertNotNull(result);
        assertEquals(testUsers.get(0).getCoreTypeList(), result);
    }

    @Test
    public void partialCoreSetSelect_should_return_a_set_of_primitives() {
        List<SimpleDbDifferentFieldTypes> testUsers = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(testUsers);

        Set<String> result = repository.partialCoreSetSelect();
        assertNotNull(result);
        assertEquals(testUsers.get(0).getCoreTypeSet(), result);
    }


    @Test
    public void partialPrimitiveSetSelect_should_return_a_list_of_primitives() {
        List<SimpleDbDifferentFieldTypes> testUsers = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(testUsers);

        Set<Float> result = repository.partialPrimitiveSetSelect();
        assertNotNull(result);
        for(SimpleDbDifferentFieldTypes simpleUser : testUsers){
            assertTrue(result.contains(simpleUser.getPrimitiveField()));
        }
    }

    @Test
    public void partialCoreMapSelect_should_return_a_list_of_primitives() {
        List<SimpleDbDifferentFieldTypes> testUsers = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(testUsers);

        Map<String, String> result = repository.partialCoreMapSelect();
        assertNotNull(result);
        assertEquals(testUsers.get(0).getCoreTypeMap(), result);
    }


    @Test
    @Ignore
    public void partialListOfListField_should_return_a_list_of_core_object_fields() {
        List<SimpleDbDifferentFieldTypes> testUsers = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<List<Integer>> result = repository.partialListOfCoreTypeListSelect();
        assertNotNull(result);
        for(SimpleDbDifferentFieldTypes simpleUser : testUsers){
            assertTrue(result.contains(simpleUser.getCoreTypeList()));
        }
    }
}
