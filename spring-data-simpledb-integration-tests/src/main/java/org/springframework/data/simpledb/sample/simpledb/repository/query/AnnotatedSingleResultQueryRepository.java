package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

interface AnnotatedSingleResultQueryRepository extends PagingAndSortingRepository<SimpleDbUser, String> {

	@Query(select = "count(*)")
	Long customLongCount();

	@Query(select = "count(*)")
	long customlongCount();

	@Query(select = "count(*)")
	int customIntCount();

	@Query(where = "itemName()='Item_0'")
	SimpleDbUser customSelectOneUser();

	@Query
	SimpleDbUser customFailSelectOneUser();

	@Query(select = "primitiveField", where = "itemName()='Item_0'")
	float partialPrimitiveFieldSelect();
}
