package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.amazonaws.services.simpledb.AmazonSimpleDB;

/**
 * Interface that specifies a basic set of SimpleDB operations.
 */
public interface ISimpleDBOperations {
	
	/**
	 * The domain name used for the specified class by this template.
	 * 
	 * @param entityClass must not be {@literal null}.
	 * @return the domain name for the specified class
	 */
	String getDomainName(Class<?> entityClass);

	AmazonSimpleDB getDB();
	
	<T> T createOrUpdateItem(T entity);

	void deleteItem(String domainName, String itemName);

	<T, ID extends Serializable> T readItem(ID id, Class<T> entityClass, boolean consistentRead);

	<T> long count(Class<T> entityClass, boolean consistentRead);

	<T> List<T> find(Class<T> entityClass, String query, boolean consistentRead);

	<T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable,
			boolean consistentRead);
}
