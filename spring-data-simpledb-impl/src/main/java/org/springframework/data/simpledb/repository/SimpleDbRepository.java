package org.springframework.data.simpledb.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

public interface SimpleDbRepository <T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

    //Add here simpledb specific crud operations

//    /*
//     * (non-Javadoc)
//     * @see org.springframework.data.repository.CrudRepository#findAll()
//     */
//    List<T> findAll();
//
//    /*
//     * (non-Javadoc)
//     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
//     */
//    List<T> findAll(Sort sort);
//
//    /*
//     * (non-Javadoc)
//     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Iterable)
//     */
//    <S extends T> List<S> save(Iterable<S> entities);
//
//    /**
//     * Flushes all pending changes to the database.
//     */
//    void flush();
//
//    /**
//     * Saves an entity and flushes changes instantly.
//     *
//     * @param entity
//     * @return the saved entity
//     */
//    T saveAndFlush(T entity);
//
//    /**
//     * Deletes the given entities in a batch which means it will create a single {@link org.springframework.data.jpa.repository.Query}. Assume that we will clear
//     * the {@link javax.persistence.EntityManager} after the call.
//     *
//     * @param entities
//     */
//    void deleteInBatch(Iterable<T> entities);
//
//    /**
//     * Deletes all entites in a batch call.
//     */
//    void deleteAllInBatch();
}
