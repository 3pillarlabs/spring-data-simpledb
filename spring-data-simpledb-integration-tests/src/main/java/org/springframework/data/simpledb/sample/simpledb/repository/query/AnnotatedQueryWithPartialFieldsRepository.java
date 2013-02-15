package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

interface AnnotatedQueryWithPartialFieldsRepository extends PagingAndSortingRepository<SimpleDbUser, String> {
    @Query(value = "select coreField from `testDB.simpleDbUser` where itemName()=:itemname")
    List<List<Object>> selectCoreFieldByItemName(@Param("itemname") String itemName);

    @Query(value = "select primitiveField,coreField from `testDB.simpleDbUser` where itemName()=:itemname")
    List<List<Object>> selectPrimitiveFieldAndCoreFieldByItemName(@Param("itemname") String itemName);

    @Query(value = "select coreField from `testDB.simpleDbUser`")
    List<List<Object>> selectCoreFields();

    @Query(value = "select primitiveField,coreField from `testDB.simpleDbUser`")
    List<List<Object>> selectPrimitiveFieldsAndCoreFields();


}
