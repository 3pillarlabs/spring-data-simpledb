package org.springframework.data.simpledb.repository.support;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.core.SimpleDbTemplate;
import org.springframework.util.Assert;

public class SimpleDbRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends
        RepositoryFactoryBeanSupport<T, S, ID> {

    @Autowired
    private SimpleDbOperations simpleDbOperations; //implement all SimpleDbRepository with operations defined in SimpleDbOperations

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        System.out.println("================"+((SimpleDbTemplate)simpleDbOperations).getAccessID());
        return new SimpleDbRepositoryFactory();
    }
}
