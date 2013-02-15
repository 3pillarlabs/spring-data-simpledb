package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

import java.util.List;

public interface AnnotatedQueryWithPartialFieldsReqpository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select coreField from `testDB.simpleDbUser` where itemName()=:itemname")
    public List<List<Object>> selectCoreFieldByItemName(@Param("itemname") String itemName);

    @Query(value = "select primitiveField,coreField from `testDB.simpleDbUser` where itemName()=:itemname")
    public List<List<Object>> selectPrimitiveField_CoreFieldByItemName(@Param("itemname") String itemName);

    @Query(value = "select coreField from `testDB.simpleDbUser`")
    public List<List<Object>> selectCoreFields();

    @Query(value = "select primitiveField,coreField from `testDB.simpleDbUser`")
    public List<List<Object>> selectPrimitiveFields_CoreFields();
}
