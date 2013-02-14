package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AnnotatedQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select * from `testDB.simpleDbUser`")
    List<SimpleDbUser> customSelectAll();

    @Query("select * from `testDB.simpleDbUser` where Item_1 = :item1 and primitiveField = :primitiveField")
    List<SimpleDbUser> customSelectWithNamedParamsQuery(@Param(value="primitiveField") String primitiveField, @Param(value="item1") String coreField);


    @Query("select * from `testDB.simpleDbUser` where coreField = ? and primitiveField = ?")
    List<SimpleDbUser> customSelectWithIndexedParams(String coreField, String primitiveField);

    @Query(value = "select * from `testDB.simpleDbUser`")
    List<String> customSelectAllWrongReturnType();

}
