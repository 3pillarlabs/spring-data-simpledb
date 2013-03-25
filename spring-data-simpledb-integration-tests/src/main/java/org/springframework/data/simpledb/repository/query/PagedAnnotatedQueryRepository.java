package org.springframework.data.simpledb.repository.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.domain.SimpleDbUser;

public interface PagedAnnotatedQueryRepository extends CrudRepository<SimpleDbUser, String> {

	@Query(where = "primitiveField > ?")
	Page<SimpleDbUser> findUsersWithPrimitiveFieldHigherThan(float primitiveField, Pageable page);

	@Query(where = "primitiveField > ?")
	List<SimpleDbUser> findUserListWithPrimitiveFieldHigherThan(float primitiveField, Pageable page);

	@Query(where = "someMissingField > ? ?")
	Page<SimpleDbUser> invalidQuery(Pageable page);

	@Query(select = "primitiveField", where = "primitiveField > ?")
	List<SimpleDbUser> pagedPartialQuery(float primitiveField, Pageable page);

}
