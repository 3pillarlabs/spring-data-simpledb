package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbDifferentFieldTypes;

import java.util.List;
import java.util.Map;
import java.util.Set;

interface AnnotatedQueryWithSinglePartialFieldRepository extends PagingAndSortingRepository<SimpleDbDifferentFieldTypes, String> {

    @Query(value = "SELECT primitiveField FROM `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    float partialPrimitiveFieldSelect();

    @Query(value = "SELECT objectList FROM `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    List<JSONCompatibleClass> partialObjectListSelect();

    @Query(value = "SELECT jsonCompatibleClass FROM `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    JSONCompatibleClass partialNestedEntitySelect();

    @Query(value = "SELECT coreTypeList FROM `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    List<Integer> partialCoreTypeListSelect();

    @Query(value = "Select primitiveField from `testDB.simpleDbDifferentFieldTypes`")
    List<Float> partialPrimitiveListSelect();

    @Query(value = "select coreTypeSet from `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    Set<String> partialCoreSetSelect();

    @Query(value = "select coreTypeMap from `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    Map<String, String> partialCoreMapSelect();

    @Query(value = "Select primitiveField from `testDB.simpleDbDifferentFieldTypes`")
    Set<Float> partialPrimitiveSetSelect();

    @Query(value = "SELECT coreTypeList FROM `testDB.simpleDbDifferentFieldTypes`")
    List<List<Integer>> partialListOfCoreTypeListSelect();

    @Query(value = "SELECT coreTypeList FROM `testDB.simpleDbDifferentFieldTypes`")
    List<SimpleDbDifferentFieldTypes> partialDomainClassListSelect();


    @Query(value = "SELECT jsonCompatibleClass FROM `testDB.simpleDbDifferentFieldTypes`")
    List<JSONCompatibleClass> partialMultipleNestedEntitySelect();
}
