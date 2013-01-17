/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.simpledb.repository.support;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.LockMetadataProvider;
import org.springframework.data.simpledb.repository.SimpleDbRepository;
import org.springframework.data.simpledb.core.SimpleDbEntity;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.simpledb.core.SimpleDbOperations;

/**
 * Default implementation of the {@link org.springframework.data.repository.CrudRepository} interface. This will offer you a more sophisticated interface than the plain
 * {@link javax.persistence.EntityManager} .
 *
 * @author Oliver Gierke
 * @author Eberhard Wolff
 * @param <T> the type of the entity to handle
 * @param <ID> the type of the entity's identifier
 */
@org.springframework.stereotype.Repository
@Transactional(readOnly = true)
public class SimpleSimpleDbRepository<T, ID extends Serializable> implements SimpleDbRepository<T, ID> {

    private final SimpleDbEntityInformation<T, ?> entityInformation;
    private LockMetadataProvider lockMetadataProvider;
    private SimpleDbOperations<T, ?> operations;

    /**
     * Creates a new {@link org.springframework.data.jpa.repository.support.SimpleJpaRepository} to manage objects of the given
     * {@link org.springframework.data.jpa.repository.support.JpaEntityInformation}.
     *
     * @param entityInformation must not be {@literal null}.
     * @param simpledbOperations
     */
    public SimpleSimpleDbRepository(SimpleDbEntityInformation<T, ?> entityInformation, SimpleDbOperations<T, ?> simpledbOperations) {
        Assert.notNull(simpledbOperations);

        this.operations = simpledbOperations;

        Assert.notNull(entityInformation);

        this.entityInformation = entityInformation;
    }

    /**
     * Configures a custom {@link org.springframework.data.jpa.repository.support.LockMetadataProvider} to be used to detect {@link javax.persistence.LockModeType}s to be applied to queries.
     *
     * @param lockMetadataProvider
     */
    public void setLockMetadataProvider(LockMetadataProvider lockMetadataProvider) {
        this.lockMetadataProvider = lockMetadataProvider;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
     */
    @Transactional
    @Override
    public void delete(ID id) {

        Assert.notNull(id, "The given id must not be null!");

        if (!exists(id)) {
            throw new EmptyResultDataAccessException(String.format("No %s entity with id %s exists!",
                    entityInformation.getJavaType(), id), 1);
        }

        delete(findOne(id));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
     */
    @Transactional
    @Override
    public void delete(T entity) {
        SimpleDbEntity sdbEntity = new SimpleDbEntity(entityInformation, entity);
        Assert.notNull(entity, "The entity must not be null!");
        operations.delete(sdbEntity);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
     */
    @Transactional
    @Override
    public void delete(Iterable<? extends T> entities) {

        Assert.notNull(entities, "The given Iterable of entities not be null!");

        for (T entity : entities) {
            delete(entity);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.Repository#deleteAll()
     */
    @Transactional
    @Override
    public void deleteAll() {

        for (T element : findAll()) {
            delete(element);
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.data.repository.Repository#readById(java.io.Serializable
     * )
     */
    @Override
    public T findOne(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        return operations.findOne(entityInformation, id);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)
     */
    @Override
    public boolean exists(ID id) {

        Assert.notNull(id, "The given id must not be null!");
        return operations.exists(entityInformation, id);

    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaRepository#findAll()
     */
    @Override
    public List<T> findAll() {
        //TODO move to simpleDB Impl
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll(ID[])
     */
    @Override
    public List<T> findAll(Iterable<ID> ids) {
        //TODO move to simpleDB Impl
        return null;
    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaRepository#findAll(org.springframework.data.domain.Sort)
     */
    @Override
    public List<T> findAll(Sort sort) {
        //TODO move to simpleDB Impl
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<T> findAll(Pageable pageable) {

        if (null == pageable) {
            return new PageImpl<>(findAll());
        }
        //TODO move to simpleDB Impl
        return null;

    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#count()
     */
    @Override
    public long count() {
        //TODO move to simpleDB Impl
        return 0;
    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Object)
     */
    @Transactional
    @Override
    public <S extends T> S save(S entity) {
        SimpleDbEntity sdbEntity = new SimpleDbEntity(entityInformation, entity);
        if (entityInformation.isNew(entity)) {
            return (S) operations.addItem(sdbEntity);
        } else {
            return (S) operations.updateItem(sdbEntity);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaRepository#save(java.lang.Iterable)
     */
    @Transactional
    @Override
    public <S extends T> List<S> save(Iterable<S> entities) {

        List<S> result = new ArrayList<>();

        if (entities == null) {
            return result;
        }

        for (S entity : entities) {
            result.add(save(entity));
        }

        return result;
    }
}
