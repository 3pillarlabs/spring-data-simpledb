package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;


public interface NamedQueryRepository extends CrudRepository<SimpleDbUser, String>{

	SimpleDbUser findByItemNameAndPrimitiveFieldGreaterThanOrCoreFieldLike(final String itemName, final float primitiveField, String like);
}
