package org.springframework.data.simpledb.repository.support;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.data.simpledb.repository.simpledb.SimpleDbImpl;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.util.Assert;

/**
 * Constructs additional elements needed by the repository factory
 * i.e. EntityManager for JPA, Some SimpleDbImpl client class
 * Returns repository creation factory.
 */
public class SimpleDbRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends
        RepositoryFactoryBeanSupport<T, S, ID> {


    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        SimpleDbImpl<?, Serializable> simpleDb = new SimpleDbImpl();

        return new SimpleDbRepositoryFactory(simpleDb);
    }
}
