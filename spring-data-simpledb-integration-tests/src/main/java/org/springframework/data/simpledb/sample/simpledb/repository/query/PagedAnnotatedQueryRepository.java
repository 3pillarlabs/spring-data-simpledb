package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

public interface PagedAnnotatedQueryRepository extends CrudRepository<SimpleDbUser, String> {
	
    @Query(value = "select * from `testDB.simpleDbUser` where primitiveField > ?")
    Page<SimpleDbUser> findUsersWithPrimitiveFieldHigherThan(float primitiveField, Pageable page);

    @Query(value = "select * from `testDB.simpleDbUser` where primitiveField > ?")
    List<SimpleDbUser> findUserListWithPrimitiveFieldHigherThan(float primitiveField, Pageable page);
    
    @Query(value = "select * from `not.valid.db`")
    Page<SimpleDbUser> invalidQuery(Pageable page);
    
    @Query(value = "select primitiveField from `testDB.simpleDbUser` where primitiveField > ?")
    List<SimpleDbUser> pagedPartialQuery(float primitiveField, Pageable page);

}
