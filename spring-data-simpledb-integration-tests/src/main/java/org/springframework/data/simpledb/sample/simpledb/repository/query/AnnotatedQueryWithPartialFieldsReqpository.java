package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

public interface AnnotatedQueryWithPartialFieldsReqpository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select primitiveField from `testDB.simpleDbUser` where itemName()=:itemname")
    public List<List<Object>> selectSinglePartialField(@Param("itemname") String itemName);

    @Query(value = "select primitiveField,coreField from `testDB.simpleDbUser` where itemName()=:itemname")
    public List<List<Object>> selectMultiplePartialField(@Param("itemname") String itemName);

    @Query(value = "select primitiveField from `testDB.simpleDbUser`")
    public List<List<Object>> selectSinglePartialFieldList();

    @Query(value = "select primitiveField,coreField from `testDB.simpleDbUser`")
    public List<List<Object>> selectMultiplePartialFieldList();
}
