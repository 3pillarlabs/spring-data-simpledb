package org.springframework.data.simpledb.repository.support;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy;
import org.springframework.data.jpa.repository.query.QueryExtractor;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.simpledb.repository.SimpleDbRepository;
import org.springframework.util.Assert;

import static org.springframework.data.querydsl.QueryDslUtils.QUERY_DSL_PRESENT;
import org.springframework.data.simpledb.core.SimpleDbOperations;

/**
 * SimpleDB specific generic repository factory.
 */
public class SimpleDbRepositoryFactory extends RepositoryFactorySupport {

    private final LockModeRepositoryPostProcessor lockModePostProcessor;

    private final SimpleDbOperations simpleDbOperations;

    public SimpleDbRepositoryFactory(SimpleDbOperations simpleDbOperations) {

        this.lockModePostProcessor = LockModeRepositoryPostProcessor.INSTANCE;
        this.simpleDbOperations = simpleDbOperations;

        addRepositoryProxyPostProcessor(lockModePostProcessor);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getTargetRepository(org.springframework.data.repository.core.RepositoryMetadata)
     */
    @Override
    protected Object getTargetRepository(RepositoryMetadata metadata) {
        SimpleDbEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

        SimpleSimpleDbRepository<?, ?> repo =  new SimpleSimpleDbRepository(entityInformation,simpleDbOperations);
        repo.setLockMetadataProvider(lockModePostProcessor.getLockMetadataProvider());

        return repo;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.data.repository.support.RepositoryFactorySupport#
     * getRepositoryBaseClass()
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return SimpleSimpleDbRepository.class;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.data.repository.support.RepositoryFactorySupport#
     * getQueryLookupStrategy
     * (org.springframework.data.repository.query.QueryLookupStrategy.Key)
     */
    @Override
    protected QueryLookupStrategy getQueryLookupStrategy(QueryLookupStrategy.Key key) {

//        return JpaQueryLookupStrategy.create(entityManager, key, extractor);
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.data.repository.support.RepositoryFactorySupport#
     * getEntityInformation(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T, ID extends Serializable> SimpleDbEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {

        return (SimpleDbEntityInformation<T, ID>) SimpleDbEntityInformationSupport.getMetadata(domainClass);
    }

}
