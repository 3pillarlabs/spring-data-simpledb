package org.springframework.data.simpledb.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.core.SimpleDbOperations;

/**
 * Convenience type for access to finder methods of enclosing 
 * {@link SimpleDbOperations} object.
 * 
 * @author Sayantam Dey
 *
 */
public class SdbItemQuery<T> {

	private final SimpleDbOperations simpleDbOps;
	private final String query;
	private final Class<T> entityClass;
	private final boolean defaultConsistentRead;
	
	public SdbItemQuery(Class<T> entityClass, String query, SimpleDbOperations simpleDbOps) {
		this.simpleDbOps = simpleDbOps;
		this.query = query;
		this.entityClass = entityClass;
		this.defaultConsistentRead = simpleDbOps.getSimpleDb().isConsistentRead();
	}

	public List<T> find() {
		return find(defaultConsistentRead);
	}

	public List<T> find(boolean consistentRead) {
		return simpleDbOps.find(entityClass, query, consistentRead);
	}

	public Page<T> executePagedQuery(Pageable pageable) {
		return executePagedQuery(pageable, defaultConsistentRead);
	}

	public Page<T> executePagedQuery(Pageable pageable, boolean consistentRead) {
		return simpleDbOps.executePagedQuery(entityClass, query, pageable, consistentRead);
	}

	public long count() {
		return count(defaultConsistentRead);
	}

	public long count(boolean consistentRead) {
		return simpleDbOps.count(query, entityClass, consistentRead);
	}
	
	
	
}
