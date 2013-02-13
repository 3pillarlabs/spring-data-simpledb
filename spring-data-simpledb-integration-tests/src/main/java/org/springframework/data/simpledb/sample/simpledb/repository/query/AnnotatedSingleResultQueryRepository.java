package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

public interface AnnotatedSingleResultQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select count(*) from `testDB.simpleDbUser`")
    public Long customLongCount();

    @Query(value = "select count(*) from `testDB.simpleDbUser`")
    public long customlongCount();

    @Query(value = "select count(*) from `testDB.simpleDbUser`")
    public int customIntCount();

    @Query(value = "select count (*) from `testDB.simpleDbUser`")
    public Long customCountWrongSelect();

    @Query(value = "select * from `testDB.simpleDbUser` where itemName()='Item_0'")
    public SimpleDbUser customSelectOneUser();

    @Query(value = "select * from `testDB.simpleDbUser`")
    public SimpleDbUser customFailSelectOneUser();
}
