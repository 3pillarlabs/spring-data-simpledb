package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

import java.util.List;
import org.springframework.data.repository.query.Param;

public interface AnnotatedQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select * from `testDB.simpleDbUser`")
    public List<SimpleDbUser> customSelectAll();

    @Query(value = "select count(*) from `testDB.simpleDbUser`")
    public long customCount();

    @Query(value = "select * from `testDB.simpleDbUser` where itemName()='Item_0'")
    public SimpleDbUser customSelectOneUser();

    @Query(value = "select primitiveField from `testDB.simpleDbUser` where itemName()=:itemname")
    public float partialPrimitiveFieldSelect(@Param("itemname") String itemName);

    @Query(value = "select primitiveField from `testDB.simpleDbUser`")
    public List<Float> partialPrimitiveListSelect();

    @Query(value = "select coreField from `testDB.simpleDbUser` where itemName()='Item_0'")
    public String partialCoreFieldSelect();

    @Query(value = "select coreField from `testDB.simpleDbUser`")
    public List<String> partialCoreListSelect();


}
