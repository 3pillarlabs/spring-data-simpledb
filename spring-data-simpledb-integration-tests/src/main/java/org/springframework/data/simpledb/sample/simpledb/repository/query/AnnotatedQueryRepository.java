package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

import java.util.List;

public interface AnnotatedQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select * from `testDB.simpleDbUser`")
    public List<SimpleDbUser> customSelectAll();

    @Query(value = "select count(*) from `testDB.simpleDbUser`")
    public long customCount();

    @Query(value = "select * from `testDB.simpleDbUser` where itemName()='Item_0'")
    public SimpleDbUser customSelectOneUser();
}
