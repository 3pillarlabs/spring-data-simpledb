package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.query.SdbItemQuery;

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
	
	<T, ID> void delete(Class<T> entityClass, ID id);
	
	<T> void delete(Iterable<? extends T> entities);
	
	<T, ID> void delete(Class<T> entityClass, Iterable<? extends ID> id);
	
	<T> void deleteAll(Class<T> entityClass);
	
	<T, ID extends Serializable> T read(ID id, Class<T> entityClass);

	<T, ID extends Serializable> T read(ID id, Class<T> entityClass, boolean consistentRead);

	<T> long count(Class<T> entityClass);

	<T> long count(Class<T> entityClass, boolean consistentRead);

	<T> long count(String query, Class<T> entityClass);

	<T> long count(String query, Class<T> entityClass, boolean consistentRead);

	<T> List<T> findAll(Class<T> entityClass);

	<T> List<T> findAll(Class<T> entityClass, boolean consistentRead);

	<T> List<T> find(Class<T> entityClass, String query);

	<T> List<T> find(Class<T> entityClass, String query, boolean consistentRead);

	<T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable);

	<T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable, boolean consistentRead);

	/**
	 * Updates an entity with the property map provided. The keys for the 
	 * property map would be the attribute names to be updated with corresponding
	 * values. In order to update nested properties, the key should be a {@code .}
	 * (dot) separated property path of the form {@code a.b.c}, where each token
	 * is a field name of the parent entity. 
	 * <p>
	 * For example:<br/>
	 * <pre>
	 * public class A {
	 *   String id;
	 *   B b;
	 *   // getters & setters not shown
	 * }
	 * 
	 * public class B {
	 *   String id;
	 *   String name;
	 *   // getters & setters not shown
	 * }
	 * </pre>
	 * In order to perform a selective update of only {@code B#name}, given an 
	 * instance of class A:
	 * <pre>
	 * // simpleDbOperations = ...; (injected)
	 * Map<String, Object> map = new HashMap<String, Object>();
	 * map.put("b.name", "droid");
	 * simpleDbOperations.update(A.class, "qxua", map); 
	 * </pre> 
	 * <p>
	 * For an candidate attribute, if it is a nested entity or a Map, the key
	 * is used as <i>prefix</i>. The nested entity must not be a {@code Reference}
	 * entity, the behavior can not be guaranteed in this case.
	 * <p>
	 * The consistent read property of this operation will be determined by
	 * {@link SimpleDb#setConsistentRead(boolean)} setting.
	 * 
	 * @param entityClass
	 * @param id
	 * @param propertyMap
	 */
	<T, ID> void update(ID id, Class<T> entityClass, Map<String, Object> propertyMap);

	/**
	 * Creates an object suitable for use with any of the query based methods
	 * (and <i>consistentRead</i> variations) on {@link SimpleDbOperations}:
	 * <ul>
	 * <li>{@link #find(Class, String)}
	 * <li>{@link #executePagedQuery(Class, String, Pageable)}
	 * <li>{@link #count(String, Class)}
	 * </ul>
	 * 
	 * @param entityClass
	 * @param whereClause
	 * @param queryParams
	 * @return a {@link SdbItemQuery} object for executing the finder methods
	 */
	<T> SdbItemQuery<T> createQuery(Class<T> entityClass, String whereClause, Object...queryParams);
}
