package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AnnotatedQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

    @Query(value = "select * from `testDB.simpleDbUser`")
    public List<SimpleDbUser> customSelectAll();
}
