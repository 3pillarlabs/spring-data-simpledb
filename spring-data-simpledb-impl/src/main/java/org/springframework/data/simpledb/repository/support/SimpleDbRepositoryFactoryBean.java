package org.springframework.data.simpledb.repository.support;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.core.SimpleDbOperationsImpl;

/**
 * Constructs additional elements needed by the repository factory i.e. EntityManager for JPA, Some SimpleDbOperationsImpl client class Returns repository creation factory.
 */
public class SimpleDbRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends RepositoryFactoryBeanSupport<T, S, ID> {

    @Autowired
    private SimpleDbConfig simpleDbConfig;

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        SimpleDbOperations operations = new SimpleDbOperationsImpl<>(simpleDbConfig);
        return new SimpleDbRepositoryFactory(operations);
    }
}
