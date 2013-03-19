package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.amazonaws.services.simpledb.AmazonSimpleDB;

public interface SimpleDbOperations {

	/**
	 * The domain name used for the specified class by this template.
	 * 
	 * @param entityClass
	 *            must not be {@literal null}.
	 * @return the domain name for the specified class
	 */
	String getDomainName(Class<?> entityClass);

	AmazonSimpleDB getDB();

	SimpleDb getSimpleDb();

	<T> T createOrUpdate(T entity);

	void delete(String domainName, String itemName);
	
	<T> void delete(T entity);
	
	<T> void delete(T entity, boolean consistentRead);

	<T> void deleteAll(Class<T> entityClass);
	
	<T> void deleteAll(Class<T> entityClass, boolean consistentRead);
	
	<T, ID extends Serializable> T read(ID id, Class<T> entityClass);

	<T, ID extends Serializable> T read(ID id, Class<T> entityClass, boolean consistentRead);

	<T> long count(Class<T> entityClass);

	<T> long count(Class<T> entityClass, boolean consistentRead);

	<T> List<T> findAll(Class<T> entityClass);

	<T> List<T> findAll(Class<T> entityClass, boolean consistentRead);

	<T> List<T> find(Class<T> entityClass, String query);

	<T> List<T> find(Class<T> entityClass, String query, boolean consistentRead);

	<T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable);

	<T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable, boolean consistentRead);

	<T> long count(String query, Class<T> entityClass);

	<T> long count(String query, Class<T> entityClass, boolean consistentRead);

}
