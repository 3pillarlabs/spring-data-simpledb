package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

interface AnnotatedSingleResultQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select count(*) from `testDB.simpleDbUser`")
    Long customLongCount();

    @Query(value = "select count(*) from `testDB.simpleDbUser`")
    long customlongCount();

    @Query(value = "select count(*) from `testDB.simpleDbUser`")
    int customIntCount();

    @Query(value = "select * from `testDB.simpleDbUser` where itemName()='Item_0'")
    SimpleDbUser customSelectOneUser();

    @Query(value = "select * from `testDB.simpleDbUser`")
    SimpleDbUser customFailSelectOneUser();
}
