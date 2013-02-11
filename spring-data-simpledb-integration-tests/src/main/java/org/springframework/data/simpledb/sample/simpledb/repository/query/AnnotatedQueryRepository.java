package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.query.Param;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AnnotatedQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select * from `testDB.simpleDbUser`")
    List<SimpleDbUser> customSelectAll();

    @Query("SELECT * FROM `testDB.simpleDbUser` WHERE coreField = :coreField AND primitiveField = :primitiveField")
    List<SimpleDbUser> customSelectWithNamedParamsQuery(@Param(value="primitiveField") String primitiveField, @Param(value="coreField") String coreField);


    @Query("SELECT * FROM `testDB.simpleDbUser` WHERE coreField = ? AND primitiveField = ?")
    List<SimpleDbUser> customSelectWithIndexedParams(String coreField, String primitiveField);

}
