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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.simpledb.core.QueryBuilder;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.SimpleDbPagingAndSortingRepository;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.util.Assert;

@org.springframework.stereotype.Repository
public class SimpleDbRepositoryImpl<T, ID extends Serializable> implements PagingAndSortingRepository<T, ID>,
		SimpleDbPagingAndSortingRepository<T, ID> {

	private final SimpleDbEntityInformation<T, ID> entityInformation;
	private final SimpleDbOperations operations;
	private final boolean consistentRead;

	public SimpleDbRepositoryImpl(SimpleDbEntityInformation<T, ID> entityInformation,
			SimpleDbOperations simpledbOperations) {
		Assert.notNull(simpledbOperations);
		Assert.notNull(entityInformation);
		this.operations = simpledbOperations;
		this.entityInformation = entityInformation;
		this.consistentRead = simpledbOperations.getSimpleDb().isConsistentRead();
	}

	@Override
	public <S extends T> S save(S entity) {
		return save(entity, consistentRead);
	}

	@Override
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		return save(entities, consistentRead);
	}

	@Override
	public T findOne(ID id) {
		return findOne(id, consistentRead);
	}

	@Override
	public boolean exists(ID id) {
		return exists(id, consistentRead);
	}

	@Override
	public Iterable<T> findAll() {
		return findAll(consistentRead);
	}

	@Override
	public Iterable<T> findAll(Iterable<ID> ids) {
		return findAll(ids, consistentRead);
	}

	@Override
	public Iterable<T> findAll(Sort sort) {
		return findAll(sort, consistentRead);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		return findAll(pageable, consistentRead);
	}

	@Override
	public long count() {
		return count(consistentRead);
	}

	@Override
	public void delete(ID id) {
		delete(id, consistentRead);
	}

	@Override
	public void delete(T entity) {
		operations.delete(entity);
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		delete(entities, consistentRead);
	}

	@Override
	public void deleteAll() {
		deleteAll(consistentRead);
	}

	// --------------------------------------------------
	@Override
	public <S extends T> S save(S entity, boolean consistentRead) {
		return operations.createOrUpdate(entity);
	}

	@Override
	public <S extends T> List<S> save(Iterable<S> entities, boolean consistentRead) {
		List<S> result = new ArrayList<S>();
		if(entities == null) {
			return result;
		}
		for(S entity : entities) {
			result.add(save(entity, consistentRead));
		}
		return result;
	}

	@Override
	public T findOne(ID id, boolean consistentRead) {
		Assert.notNull(id, "The given id must not be null!");
		return operations.read(id, entityInformation.getJavaType(), consistentRead);
	}

	@Override
	public boolean exists(ID id, boolean consistentRead) {
		Assert.notNull(id, "The given id must not be null!");
		return findOne(id, consistentRead) != null;
	}

	@Override
	public List<T> findAll(boolean consistentRead) {
		return operations.find(entityInformation.getJavaType(), new QueryBuilder(entityInformation).toString(),
				consistentRead);
	}

	@Override
	public List<T> findAll(Iterable<ID> ids, boolean consistentRead) {
		return operations.find(entityInformation.getJavaType(), new QueryBuilder(entityInformation).withIds(ids)
				.toString(), consistentRead);
	}

	@Override
	public List<T> findAll(Sort sort, boolean consistentRead) {
		return operations.find(entityInformation.getJavaType(), new QueryBuilder(entityInformation).with(sort)
				.toString(), consistentRead);
	}

	@Override
	public Page<T> findAll(Pageable pageable, boolean consistentRead) {
		return operations.executePagedQuery(entityInformation.getJavaType(),
				new QueryBuilder(entityInformation).toString(), pageable, consistentRead);
	}

	@Override
	public long count(boolean consistentRead) {
		return operations.count(entityInformation.getJavaType(), consistentRead);
	}

	@Override
	public void delete(ID id, boolean consistentRead) {
		Assert.notNull(id, "The given id must not be null!");

		if(consistentRead) {
			T entity = findOne(id, consistentRead);

			if(entity == null) {
				throw new EmptyResultDataAccessException(String.format("No %s entity with id %s exists!",
						entityInformation.getJavaType(), id));
			}
		}

		operations.delete(entityInformation.getDomain(), (String) id);
	}

	@Override
	public void delete(T entity, boolean consistentRead) {
		Assert.notNull(entity, "The entity must not be null!");
		delete(entityInformation.getId(entity), consistentRead);
	}

	@Override
	public void delete(Iterable<? extends T> entities, boolean consistentRead) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");
		for(T entity : entities) {
			delete(entity, consistentRead);
		}
	}

	@Override
	public void deleteAll(boolean consistentRead) {
		operations.deleteAll(entityInformation.getJavaType());
	}
}
