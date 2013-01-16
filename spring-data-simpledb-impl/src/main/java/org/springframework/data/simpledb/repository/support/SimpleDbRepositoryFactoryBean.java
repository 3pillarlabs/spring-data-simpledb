package org.springframework.data.simpledb.repository.support;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.util.Assert;

public class SimpleDbRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends
        RepositoryFactoryBeanSupport<T, S, ID> {


    private SimpleDbOperations operations;

    public void setOperations(SimpleDbOperations operations) {
        this.operations = operations;
    }


    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new SimpleDbRepositoryFactory(operations);
    }
}
