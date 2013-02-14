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
        SimpleDbDifferentFieldTypes differentFieldsTypeEntity = SimpleDbDifferentFiledTypesBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(differentFieldsTypeEntity);

        float result = repository.partialPrimitiveFieldSelect();
        assertNotNull(result);
        assertThat(result, is(differentFieldsTypeEntity.getPrimitiveField()));
    }

    @Test
    @Ignore
    public void partialNestedEntitySelect_should_return_a_single_object_field() {
        SimpleDbDifferentFieldTypes differentFieldsTypeEntity = SimpleDbDifferentFiledTypesBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(differentFieldsTypeEntity);

        JSONCompatibleClass result = repository.partialNestedEntitySelect();
        assertNotNull(result);
        assertEquals(differentFieldsTypeEntity.getJsonCompatibleClass(), result);
    }

    @Test
    @Ignore
    public void partialObjectListSelect_should_return_a_single_object_field() {
        SimpleDbDifferentFieldTypes differentFieldsTypeEntity = SimpleDbDifferentFiledTypesBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(differentFieldsTypeEntity);

        List<JSONCompatibleClass> result = repository.partialObjectListSelect();
        assertNotNull(result);
        assertEquals(differentFieldsTypeEntity.getJsonCompatibleClass(), result);
    }

    @Test
    public void partialPrimitiveListSelect_should_return_a_list_of_primitives() {
        List<SimpleDbDifferentFieldTypes> differentFieldsTypeEntity = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(differentFieldsTypeEntity);

        List<Float> result = repository.partialPrimitiveListSelect();
        assertNotNull(result);
        for(SimpleDbDifferentFieldTypes simpleUser : differentFieldsTypeEntity){
            assertTrue(result.indexOf(simpleUser.getPrimitiveField()) != -1);
        }
    }

    @Test
    public void partialCoreTypeListSelectSelect_should_return_a_list_of_primitives() {
        SimpleDbDifferentFieldTypes differentFieldsTypeEntity = SimpleDbDifferentFiledTypesBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(differentFieldsTypeEntity);

        List<Integer> result = repository.partialCoreTypeListSelect();
        assertNotNull(result);
        assertEquals(differentFieldsTypeEntity.getCoreTypeList(), result);
    }

    @Test
    public void partialCoreSetSelect_should_return_a_set_of_primitives() {
        SimpleDbDifferentFieldTypes differentFieldsTypeEntity = SimpleDbDifferentFiledTypesBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(differentFieldsTypeEntity);

        Set<String> result = repository.partialCoreSetSelect();
        assertNotNull(result);
        assertEquals(differentFieldsTypeEntity.getCoreTypeSet(), result);
    }


    @Test
    public void partialPrimitiveSetSelect_should_return_a_set_of_primitives() {
        List<SimpleDbDifferentFieldTypes> differentFieldsTypeEntity = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(differentFieldsTypeEntity);

        Set<Float> result = repository.partialPrimitiveSetSelect();
        assertNotNull(result);
        for(SimpleDbDifferentFieldTypes simpleUser : differentFieldsTypeEntity){
            assertTrue(result.contains(simpleUser.getPrimitiveField()));
        }
    }

    @Test
    public void partialCoreMapSelect_should_return_a_map_of_primitives() {
        SimpleDbDifferentFieldTypes differentFieldsTypeEntity = SimpleDbDifferentFiledTypesBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(differentFieldsTypeEntity);

        Map<String, String> result = repository.partialCoreMapSelect();
        assertNotNull(result);
        assertEquals(differentFieldsTypeEntity.getCoreTypeMap(), result);
    }


    @Test
    @Ignore
    public void partialListOfListField_should_return_a_list_of_core_object_fields() {
        List<SimpleDbDifferentFieldTypes> differentFieldsTypeEntity = SimpleDbDifferentFiledTypesBuilder.createListOfItems(3);
        repository.save(differentFieldsTypeEntity);

        List<List<Integer>> result = repository.partialListOfCoreTypeListSelect();
        assertNotNull(result);
        for(SimpleDbDifferentFieldTypes simpleUser : differentFieldsTypeEntity){
            assertTrue(result.contains(simpleUser.getCoreTypeList()));
        }
    }
}
