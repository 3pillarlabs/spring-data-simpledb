package org.springframework.data.simpledb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.List;

public interface SimpleDbPagingAndSortingRepository<T, ID extends Serializable> extends Repository<T, ID> {

    /**
     * Retrives an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @param consistentRead true for consistent read from master node
     * @return the entity with the given id or {@literal null} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    T findOne(ID id, boolean consistentRead);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @param consistentRead true for consistent read from master node
     * @return true if an entity with the given id exists, alse otherwise
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    boolean exists(ID id, boolean consistentRead);

    /**
     * Returns all instances of the type.
     *
     * @param consistentRead true for consistent read from master node
     * @return all entities
     */
    Iterable<T> findAll(boolean consistentRead);

    /**
     * Returns all instances of the type with the given IDs.
     *
     * @param ids
     * @param consistentRead true for consistent read from master node
     * @return
     */
    Iterable<T> findAll(Iterable<ID> ids, boolean consistentRead);

    /**
     * Returns the number of entities available.
     *
     * @param consistentRead true for consistent read from master node
     * @return the number of entities
     */
    long count(boolean consistentRead);

    /**
     * Returns all entities sorted by the given options.
     *
     * @param sort
     * @param consistentRead true for consistent read from master node
     * @return all entities sorted by the given options
     */
    Iterable<T> findAll(Sort sort, boolean consistentRead);

    /**
     * Returns a {@link org.springframework.data.domain.Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @param consistentRead true for consistent read from master node
     * @return a page of entities
     */
    Page<T> findAll(Pageable pageable, boolean consistentRead);

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the entity instance completely.
     *
     * @param entity
     * @param consistentRead true for consistent read from master node
     * @return the saved entity
     */
    <S extends T> S save(S entity, boolean consistentRead);

    /**
     * Saves all given entities.
     *
     * @param entities
     * @return the saved entities
     * @param consistentRead true for consistent read from master node
     * @throws IllegalArgumentException in case the given entity is (@literal null}.
     */
    <S extends T> List<S> save(Iterable<S> entities, boolean consistentRead);

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @param consistentRead true for consistent read from master node
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    void delete(ID id, boolean consistentRead);

    /**
     * Deletes a given entity.
     *
     * @param entity
     * @throws IllegalArgumentException in case the given entity is (@literal null}.
     */
    void delete(T entity, boolean consistentRead);

    /**
     * Deletes the given entities.
     *
     * @param entities
     * @throws IllegalArgumentException in case the given {@link Iterable} is (@literal null}.
     */
    void delete(Iterable<? extends T> entities, boolean consistentRead);

    /**
     * Deletes all entities managed by the repository.
     */
    void deleteAll(boolean consistentRead);
}
