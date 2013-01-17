package org.springframework.data.simpledb.repository.support;

import java.io.Serializable;

import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

/**
 * SimpleDB specific generic repository factory.
 */
public class SimpleDbRepositoryFactory extends RepositoryFactorySupport {

    private final LockModeRepositoryPostProcessor lockModePostProcessor;
    private SimpleDbOperations<?, Serializable> simpledbOperations;

    public SimpleDbRepositoryFactory(SimpleDbOperations<?, Serializable> simpledbOperations) {
        this.simpledbOperations = simpledbOperations;

        this.lockModePostProcessor = LockModeRepositoryPostProcessor.INSTANCE;

        addRepositoryProxyPostProcessor(lockModePostProcessor);
    }



    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getTargetRepository(org.springframework.data.repository.core.RepositoryMetadata)
     */
    @Override
    protected Object getTargetRepository(RepositoryMetadata metadata) {
        SimpleDbEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

        SimpleSimpleDbRepository<?, ?> repo =  new SimpleSimpleDbRepository(entityInformation, simpledbOperations);
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
