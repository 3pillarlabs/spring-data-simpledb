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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.LockMetadataProvider;
import org.springframework.data.jpa.repository.support.PersistenceProvider;
import org.springframework.data.simpledb.repository.SimpleDbRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.data.jpa.repository.query.QueryUtils.*;
import org.springframework.data.simpledb.core.SimpleDbOperations;

/**
 * Default implementation of the {@link org.springframework.data.repository.CrudRepository} interface. This will offer
 * you a more sophisticated interface than the plain {@link javax.persistence.EntityManager} .
 *
 * @author Oliver Gierke
 * @author Eberhard Wolff
 * @param <T> the type of the entity to handle
 * @param <ID> the type of the entity's identifier
 */
@org.springframework.stereotype.Repository
@Transactional(readOnly = true)
public class SimpleSimpleDbRepository<T, ID extends Serializable> implements SimpleDbRepository<T, ID> {

    private final SimpleDbOperations simpleDbOperations; //implement all SimpleDbRepository with operations defined in SimpleDbOperations

	private final SimpleDbEntityInformation<T, ?> entityInformation;

	private LockMetadataProvider lockMetadataProvider;

	/**
	 * Creates a new {@link org.springframework.data.jpa.repository.support.SimpleJpaRepository} to manage objects of the given {@link org.springframework.data.jpa.repository.support.JpaEntityInformation}.
	 *
	 * @param entityInformation must not be {@literal null}.
	 */
	public SimpleSimpleDbRepository(SimpleDbEntityInformation<T, ?> entityInformation, SimpleDbOperations simpleDbOperations) {

		Assert.notNull(entityInformation);

		this.entityInformation = entityInformation;
        this.simpleDbOperations = simpleDbOperations;
	}

//	/**
//	 * Creates a new {@link org.springframework.data.jpa.repository.support.SimpleJpaRepository} to manage objects of the given domain type.
//	 *
//	 * @param domainClass must not be {@literal null}.
//	 */
//	public SimpleSimpleDbRepository(Class<T> domainClass) {
//		this(SimpleDbEntityInformationSupport.getMetadata(domainClass));
//	}

	/**
     * Configures a custom {@link org.springframework.data.jpa.repository.support.LockMetadataProvider} to be used to detect {@link javax.persistence.LockModeType}s to be applied to
     * queries.
     *
     * @param lockMetadataProvider
     */
    public void setLockMetadataProvider(LockMetadataProvider lockMetadataProvider) {
        this.lockMetadataProvider = lockMetadataProvider;
    }

	private Class<T> getDomainClass() {
		return entityInformation.getJavaType();
	}

//	private String getDeleteAllQueryString() {
//		return getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName());
//	}
//
//	private String getCountQueryString() {
//
//		String countQuery = String.format(COUNT_QUERY_STRING, provider.getCountQueryPlaceholder(), "%s");
//		return getQueryString(countQuery, entityInformation.getEntityName());
//	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
	 */
	@Transactional
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
	public void delete(T entity) {

		Assert.notNull(entity, "The entity must not be null!");
//		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
	 */
	@Transactional
	public void delete(Iterable<? extends T> entities) {

		Assert.notNull(entities, "The given Iterable of entities not be null!");

		for (T entity : entities) {
			delete(entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#deleteInBatch(java.lang.Iterable)
	 */
	@Transactional
	public void deleteInBatch(Iterable<T> entities) {

		Assert.notNull(entities, "The given Iterable of entities not be null!");

		if (!entities.iterator().hasNext()) {
			return;
		}

//		applyAndBind(getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName()), entities, em)
//				.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.Repository#deleteAll()
	 */
	@Transactional
	public void deleteAll() {

		for (T element : findAll()) {
			delete(element);
		}
	}

	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaRepository#deleteAllInBatch()
//	 */
//	@Transactional
//	public void deleteAllInBatch() {
//		em.createQuery(getDeleteAllQueryString()).executeUpdate();
//	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.data.repository.Repository#readById(java.io.Serializable
	 * )
	 */
	public T findOne(ID id) {

		Assert.notNull(id, "The given id must not be null!");
//		return em.find(getDomainClass(), id);
        throw new RuntimeException("De unde vrei find?");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)
	 */
	public boolean exists(ID id) {

		Assert.notNull(id, "The given id must not be null!");

//		if (entityInformation.getIdAttribute() != null) {
//
//			String placeholder = provider.getCountQueryPlaceholder();
//			String entityName = entityInformation.getEntityName();
//			String idAttributeName = entityInformation.getIdAttribute().getName();
//			String existsQuery = String.format(EXISTS_QUERY_STRING, placeholder, entityName, idAttributeName);
//
//			TypedQuery<Long> query = em.createQuery(existsQuery, Long.class);
//			query.setParameter("id", id);
//
//			return query.getSingleResult() == 1;
//		} else {
//			return findOne(id) != null;
//		}

        throw new RuntimeException("De unde vrei exists?");

    }

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#findAll()
	 */
	public List<T> findAll() {
//		return getQuery(null, (Sort) null).getResultList();
        throw new RuntimeException("De unde vrei find all?");

    }

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findAll(ID[])
	 */
	public List<T> findAll(Iterable<ID> ids) {

//		return getQuery(new Specification<T>() {
//			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Path<?> path = root.get(entityInformation.getIdAttribute());
//				return path.in(cb.parameter(Iterable.class, "ids"));
//			}
//		}, (Sort) null).setParameter("ids", ids).getResultList();
        throw new RuntimeException("De unde vrei findAll?");

    }

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#findAll(org.springframework.data.domain.Sort)
	 */
	public List<T> findAll(Sort sort) {
//		return getQuery(null, sort).getResultList();
        throw new RuntimeException("De unde vrei find all?");

    }

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable)
	 */
	public Page<T> findAll(Pageable pageable) {

		if (null == pageable) {
			return new PageImpl<T>(findAll());
		}

//		return findAll(null, pageable);
        throw new RuntimeException("De unde vrei find all?");

    }

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findOne(org.springframework.data.jpa.domain.Specification)
//	 */
//	public T findOne(Specification<T> spec) {
//
//		try {
//			return getQuery(spec, (Sort) null).getSingleResult();
//		} catch (NoResultException e) {
//			return null;
//		}
//	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification)
//	 */
//	public List<T> findAll(Specification<T> spec) {
//		return getQuery(spec, (Sort) null).getResultList();
//	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Pageable)
//	 */
//	public Page<T> findAll(Specification<T> spec, Pageable pageable) {
//
//		TypedQuery<T> query = getQuery(spec, pageable);
//		return pageable == null ? new PageImpl<T>(query.getResultList()) : readPage(query, pageable, spec);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Sort)
//	 */
//	public List<T> findAll(Specification<T> spec, Sort sort) {
//
//		return getQuery(spec, sort).getResultList();
//	}
//
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#count()
	 */
	public long count() {
//		return em.createQuery(getCountQueryString(), Long.class).getSingleResult();
        throw new RuntimeException("De unde vrei count?");

    }

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#count(org.springframework.data.jpa.domain.Specification)
//	 */
//	public long count(Specification<T> spec) {
//
//		return getCountQuery(spec).getSingleResult();
//	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(java.lang.Object)
	 */
	@Transactional
	public <S extends T> S save(S entity) {

//		if (entityInformation.isNew(entity)) {
//			em.persist(entity);
//			return entity;
//		} else {
//			return em.merge(entity);
//		}

        //TODO Implement save with simple db

        return entity;
    }

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaRepository#saveAndFlush(java.lang.Object)
//	 */
//	@Transactional
//	public T saveAndFlush(T entity) {
//
//		T result = save(entity);
//		flush();
//
//		return result;
//	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#save(java.lang.Iterable)
	 */
	@Transactional
	public <S extends T> List<S> save(Iterable<S> entities) {

		List<S> result = new ArrayList<S>();

		if (entities == null) {
			return result;
		}

		for (S entity : entities) {
			result.add(save(entity));
		}

		return result;
	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaRepository#flush()
//	 */
//	@Transactional
//	public void flush() {
//
//		em.flush();
//	}

	/**
	 * Reads the given {@link javax.persistence.TypedQuery} into a {@link org.springframework.data.domain.Page} applying the given {@link org.springframework.data.domain.Pageable} and
	 * {@link org.springframework.data.jpa.domain.Specification}.
	 *
	 * @param query must not be {@literal null}.
	 * @param spec can be {@literal null}.
	 * @param pageable can be {@literal null}.
	 * @return
	 */
//	private Page<T> readPage(TypedQuery<T> query, Pageable pageable, Specification<T> spec) {
//
//		query.setFirstResult(pageable.getOffset());
//		query.setMaxResults(pageable.getPageSize());
//
//		Long total = QueryUtils.executeCountQuery(getCountQuery(spec));
//		List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T> emptyList();
//
//		return new PageImpl<T>(content, pageable, total);
//	}

	/**
	 * Creates a new {@link javax.persistence.TypedQuery} from the given {@link org.springframework.data.jpa.domain.Specification}.
	 *
	 * @param spec can be {@literal null}.
	 * @param pageable can be {@literal null}.
	 * @return
	 */
//	private TypedQuery<T> getQuery(Specification<T> spec, Pageable pageable) {
//
//		Sort sort = pageable == null ? null : pageable.getSort();
//		return getQuery(spec, sort);
//	}

	/**
	 * Creates a {@link javax.persistence.TypedQuery} for the given {@link org.springframework.data.jpa.domain.Specification} and {@link org.springframework.data.domain.Sort}.
	 *
	 * @param spec can be {@literal null}.
	 * @param sort can be {@literal null}.
	 * @return
	 */
//	private TypedQuery<T> getQuery(Specification<T> spec, Sort sort) {
//
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		CriteriaQuery<T> query = builder.createQuery(getDomainClass());
//
//		Root<T> root = applySpecificationToCriteria(spec, query);
//		query.select(root);
//
//		if (sort != null) {
//			query.orderBy(toOrders(sort, root, builder));
//		}
//
//		return applyLockMode(em.createQuery(query));
//	}

	/**
	 * Creates a new count query for the given {@link org.springframework.data.jpa.domain.Specification}.
	 *
	 * @param spec can be {@literal null}.
	 * @return
	 */
//	private TypedQuery<Long> getCountQuery(Specification<T> spec) {
//
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		CriteriaQuery<Long> query = builder.createQuery(Long.class);
//
//		Root<T> root = applySpecificationToCriteria(spec, query);
//		query.select(builder.count(root));
//
//		return em.createQuery(query);
//	}

	/**
	 * Applies the given {@link org.springframework.data.jpa.domain.Specification} to the given {@link javax.persistence.criteria.CriteriaQuery}.
	 *
	 * @param spec can be {@literal null}.
	 * @param query must not be {@literal null}.
	 * @return
	 */
//	private <S> Root<T> applySpecificationToCriteria(Specification<T> spec, CriteriaQuery<S> query) {
//
//		Assert.notNull(query);
//		Root<T> root = query.from(getDomainClass());
//
//		if (spec == null) {
//			return root;
//		}
//
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		Predicate predicate = spec.toPredicate(root, query, builder);
//
//		if (predicate != null) {
//			query.where(predicate);
//		}
//
//		return root;
//	}
//
//	private TypedQuery<T> applyLockMode(TypedQuery<T> query) {
//
//		LockModeType type = lockMetadataProvider == null ? null : lockMetadataProvider.getLockModeType();
//		return type == null ? query : query.setLockMode(type);
//	}
}
