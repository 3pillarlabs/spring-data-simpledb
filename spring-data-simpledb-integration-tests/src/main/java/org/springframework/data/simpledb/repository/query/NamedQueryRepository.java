package org.springframework.data.simpledb.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.simpledb.domain.SimpleDbUser;

import java.util.List;

public interface NamedQueryRepository extends CrudRepository<SimpleDbUser, String>{

	SimpleDbUser findByItemName(final String itemName);
	
	List<SimpleDbUser> findByPrimitiveFieldGreaterThan(final float x);
	
	List<SimpleDbUser> getByPrimitiveFieldOrItemNameLike(final float x, final String like);
	
	List<SimpleDbUser> findByItemNameLikeOrPrimitiveFieldGreaterThanAndPrimitiveFieldLessThan(final String like, final float x1, final float x2);
	
	Page<SimpleDbUser> readByPrimitiveFieldGreaterThan(final float x, final Pageable page);
}
