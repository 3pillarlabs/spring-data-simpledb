package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

import java.util.List;
import java.util.Set;

public interface AnnotatedQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query("select * from `testDB.simpleDbUser` where Item_1 = :item1 and primitiveField = :primitiveField")
    List<SimpleDbUser> customSelectWithNamedParamsQuery(@Param(value="primitiveField") String primitiveField, @Param(value="item1") String coreField);

    @Query("select * from `testDB.simpleDbUser` where coreField = ? and primitiveField = ?")
    List<SimpleDbUser> customSelectWithIndexedParams(String coreField, String primitiveField);

    @Query(value = "select coreField from `testDB.simpleDbUser` where itemName()='Item_0'")
    List<List<Object>> selectCoreFields();

    @Query(value = "SELECT objectList FROM `testDB.simpleDbUser` where itemName()='Item_0'")
    List<JSONCompatibleClass> partialObjectListSelect();

    @Query(value = "select primitiveField from `testDB.simpleDbUser`")
    Set<Float> primitiveFieldSelect();

    @Query(value = "select * from `testDB.simpleDbUser`")
    List<String> customSelectAllWrongReturnType();

    @Query(where = "itemName = 'Item_0'")
    List<SimpleDbUser> customSelectWithWhereClause();
}
