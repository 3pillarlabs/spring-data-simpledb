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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.core.SelectQueryBuilder;
import org.springframework.data.simpledb.core.SimpleDbEntity;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Repository
public class SimpleSimpleDbRepository<T, ID extends Serializable> implements PagingAndSortingRepository<T, ID> {

    private final SimpleDbEntityInformation<T, ID> entityInformation;
    private SimpleDbOperations<T, ID> operations;

    public SimpleSimpleDbRepository(SimpleDbEntityInformation<T, ID> entityInformation, SimpleDbOperations<T, ID> simpledbOperations) {
        Assert.notNull(simpledbOperations);
        Assert.notNull(entityInformation);
        this.operations = simpledbOperations;
        this.entityInformation = entityInformation;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Object)
     */
    @Override
    public <S extends T> S save(S entity) {
        SimpleDbEntity sdbEntity = new SimpleDbEntity(entityInformation, entity);
        if (entityInformation.isNew(entity)) {
            return (S) operations.createItem(sdbEntity);
        } else {
            return (S) operations.updateItem(sdbEntity);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaRepository#save(java.lang.Iterable)
     */
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

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
     */
    @Override
    public void delete(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        if (!exists(id)) {
            throw new EmptyResultDataAccessException(String.format("No %s entity with id %s exists!", entityInformation.getJavaType(), id));
        }
        delete(findOne(id));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
     */
    @Override
    public void delete(T entity) {
        Assert.notNull(entity, "The entity must not be null!");
        SimpleDbEntity sdbEntity = new SimpleDbEntity(entityInformation, entity);
        operations.deleteItem(sdbEntity);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
     */
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
        return operations.readItem(entityInformation, id);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)
     */
    @Override
    public boolean exists(ID id) {
        Assert.notNull(id, "The given id must not be null!");
        return findOne(id) != null;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaRepository#findAll()
     */
    @Override
    public List<T> findAll() {
        return operations.find(entityInformation, new SelectQueryBuilder(entityInformation));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll(FIELD_NAME_DEFAULT_ID[])
     */
    @Override
    public List<T> findAll(Iterable<ID> ids) {
        return operations.find(entityInformation, new SelectQueryBuilder(entityInformation).with(ids));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaRepository#findAll(org.springframework.data.domain.Sort)
     */
    @Override
    public List<T> findAll(Sort sort) {
        return operations.find(entityInformation, new SelectQueryBuilder(entityInformation).with(sort));
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
        Long count = count();
        List<T> list = operations.find(entityInformation, new SelectQueryBuilder(entityInformation).with(pageable));
        return new PageImpl<>(list, pageable, count);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#count()
     */
    @Override
    public long count() {
        return operations.count(entityInformation);
    }
}
