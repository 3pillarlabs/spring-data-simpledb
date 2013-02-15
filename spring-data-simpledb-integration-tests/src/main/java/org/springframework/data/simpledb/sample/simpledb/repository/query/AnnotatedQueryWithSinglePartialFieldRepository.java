package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbDifferentFieldTypes;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AnnotatedQueryWithSinglePartialFieldRepository extends PagingAndSortingRepository<SimpleDbDifferentFieldTypes, String> {

    @Query(value = "SELECT primitiveField FROM `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    public float partialPrimitiveFieldSelect();

    @Query(value = "SELECT objectList FROM `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    public List<JSONCompatibleClass> partialObjectListSelect();

    @Query(value = "SELECT jsonCompatibleClass FROM `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    public JSONCompatibleClass partialNestedEntitySelect();

    @Query(value = "SELECT coreTypeList FROM `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    public List<Integer> partialCoreTypeListSelect();

    @Query(value = "Select primitiveField from `testDB.simpleDbDifferentFieldTypes`")
    public List<Float> partialPrimitiveListSelect();

    @Query(value = "select coreField from `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    public String partialCoreFieldSelect();

    @Query(value = "select coreField from `testDB.simpleDbDifferentFieldTypes`")
    public List<String> partialCoreListSelect();

    @Query(value = "select coreTypeSet from `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    public Set<String> partialCoreSetSelect();

    @Query(value = "select coreTypeMap from `testDB.simpleDbDifferentFieldTypes` where itemName()='Item_0'")
    public Map<String, String> partialCoreMapSelect();

    @Query(value = "Select primitiveField from `testDB.simpleDbDifferentFieldTypes`")
    public Set<Float> partialPrimitiveSetSelect();

    @Query(value = "Select primitiveField from `testDB.simpleDbDifferentFieldTypes`")
    public List<List<Object>> partialGenericSelect();

    @Query(value = "Select primitiveField from `testDB.simpleDbDifferentFieldTypes`")
    public List<SimpleDbDifferentFieldTypes> partialGenericDomainSelect();

    @Query(value = "SELECT coreTypeList FROM `testDB.simpleDbDifferentFieldTypes`")
    public List<List<Integer>> partialListOfCoreTypeListSelect();
}
