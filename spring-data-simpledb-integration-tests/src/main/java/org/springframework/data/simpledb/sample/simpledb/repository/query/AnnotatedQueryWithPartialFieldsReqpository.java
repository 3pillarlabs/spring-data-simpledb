package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

public interface AnnotatedQueryWithPartialFieldsReqpository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select primitiveField from `testDB.simpleDbUser` where itemName()=:itemname")
    public float partialPrimitiveFieldSelect(@Param("itemname") String itemName);

    @Query(value = "select primitiveField from `testDB.simpleDbUser`")
    public List<Float> partialPrimitiveListSelect();

    @Query(value = "select coreField from `testDB.simpleDbUser` where itemName()='Item_0'")
    public String partialCoreFieldSelect();

    @Query(value = "select coreField from `testDB.simpleDbUser`")
    public List<String> partialCoreListSelect();
}
