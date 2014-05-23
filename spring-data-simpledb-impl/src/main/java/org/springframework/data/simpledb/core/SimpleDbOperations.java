package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.simpledb.query.SdbItemQuery;

import com.amazonaws.services.simpledb.AmazonSimpleDB;

/**
 * Interface for direct usage of SimpleDb and internal use by Spring Data
 * repository components.
 *
 */
public interface SimpleDbOperations {

	/**
	 * The domain name used for the specified class by this template.
	 * 
	 * @param entityClass
	 *            must not be {@literal null}.
	 * @return the domain name for the specified class
	 */
	String getDomainName(Class<?> entityClass);

	/**
	 * @return Amazon SimpleDb client instance
	 */
	AmazonSimpleDB getDB();

	/**
	 * @return SimpleDb configuration bean
	 */
	SimpleDb getSimpleDb();

	/**
	 * Creates or updates an entity. The decision to create or update depends
	 * on the enitity Id, if null, the entity is created, updated otherwise.
	 * <p>
	 * For updates this is an expensive process, since the implementation may
	 * delete attributes and then update the attributes since there is no track
	 * of state changes in the entity. For more efficient way to update, see
	 * {@link #update(Object, Class, Map)}.
	 * 
	 * @param entity
	 * @return T
	 */
	<T> T createOrUpdate(T entity);

	/**
	 * Deletes an item based on domainName and itemName (id). 
	 * 
	 * @param domainName
	 * @param itemName
	 */
	void delete(String domainName, String itemName);
	
	/**
	 * Deletes an entity from SimpleDb.
	 * 
	 * @param entity
	 */
	<T> void delete(T entity);
	
	/**
	 * Deletes an item from SimpleDb, where the item is identified by the entity
	 * class and id.
	 * 
	 * @param entityClass
	 * @param id
	 */
	<T, ID> void delete(Class<T> entityClass, ID id);
	
	/**
	 * Delete more than one entity with single method.
	 * 
	 * @param entities
	 */
	<T> void delete(Iterable<? extends T> entities);
	
	/**
	 * Delete more than one item, where each item is identified by the entity class
	 * and id.
	 * 
	 * @param entityClass
	 * @param id
	 */
	<T, ID> void delete(Class<T> entityClass, Iterable<? extends ID> id);
	
	/**
	 * Deletes all entities.
	 * <p>
	 * <b>Warning: This will delete all items in the entity domain!</b>
	 * 
	 * @param entityClass
	 */
	<T> void deleteAll(Class<T> entityClass);
	
	/**
	 * Find an entity by id.
	 * 
	 * @param id
	 * @param entityClass
	 * @return T
	 */
	<T, ID extends Serializable> T read(ID id, Class<T> entityClass);

	/**
	 * Find an entity by id.
	 * 
	 * @param id
	 * @param entityClass
	 * @param consistentRead
	 * @return T
	 */
	<T, ID extends Serializable> T read(ID id, Class<T> entityClass, boolean consistentRead);

	/**
	 * @param entityClass
	 * @return count of items in the domain for entityClass
	 */
	<T> long count(Class<T> entityClass);

	/**
	 * @param entityClass
	 * @param consistentRead
	 * @return count of items in the domain for entityClass
	 */
	<T> long count(Class<T> entityClass, boolean consistentRead);

	/**
	 * @param query 
	 *               this needs to be a full query with select, from and where clause
	 * @param entityClass
	 * @return count of matching items in the domain for entityClass
	 */
	<T> long count(String query, Class<T> entityClass);

	/**
	 * @param query
	 *              this needs to be a full query with select, from and where clause
	 * @param entityClass
	 * @param consistentRead
	 * @return count of matching items in the domain for entityClass
	 */
	<T> long count(String query, Class<T> entityClass, boolean consistentRead);

	/**
	 * Finds all entities in a domain subject to single fetch rules. In other
	 * words, more items may exist, this method does not handle pagination.
	 * 
	 * @param entityClass
	 * @return List of T
	 */
	<T> List<T> findAll(Class<T> entityClass);

	/**
	 * Overloaded form of {@link #findAll(Class)} with option to reverse the
	 * default consistentRead setting.
	 *  
	 * @param entityClass
	 * @param consistentRead
	 * @return List of T
	 */
	<T> List<T> findAll(Class<T> entityClass, boolean consistentRead);

	/**
	 * Finds all entities matching the query, does not handle pagination.
	 * 
	 * @param entityClass
	 * @param query needs to be a full query with select, from and where clauses.
	 * @return List of T
	 */
	<T> List<T> find(Class<T> entityClass, String query);

	/**
	 * Overloaded form of {@link #find(Class, String)} with option to reverse the
	 * default consistentRead setting.
	 * 
	 * @param entityClass
	 * @param query
	 * @param consistentRead
	 * @return List of T
	 */
	<T> List<T> find(Class<T> entityClass, String query, boolean consistentRead);

	/**
	 * Paginated finder method.
	 * 
	 * @param entityClass
	 * @param query
	 * @param pageable
	 * @return Page of T
	 * @see Page
	 */
	<T> Page<T> executePagedQuery(Class<T> entityClass, String query, Pageable pageable);

	/**
	 * Overloaded form of {@link #executePagedQuery(Class, String, Pageable)}
	 * with option to reverse the default consistentRead setting.
	 * 
	 * @param entityClass
	 * @param query
	 * @param pageable
	 * @param consistentRead
	 * @return Page of T
	 */
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
	 * simpleDbOperations.update("qxua", A.class, map); 
	 * </pre> 
	 * <p>
	 * For a candidate attribute, if it is a nested entity or a Map, the key
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
