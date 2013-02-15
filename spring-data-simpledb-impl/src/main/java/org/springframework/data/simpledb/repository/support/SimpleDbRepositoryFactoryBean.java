package org.springframework.data.simpledb.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.simpledb.core.SimpleDbConfig;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;

/**
 * Constructs additional elements needed by the repository factory i.e. EntityManager for JPA, Some SimpleDbOperationsImpl client class Returns repository creation factory.
 */
public class SimpleDbRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends RepositoryFactoryBeanSupport<T, S, ID> {

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        AmazonSimpleDB sdb = new AmazonSimpleDBClient(new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return SimpleDbConfig.getInstance().getAccessID();
            }

            @Override
            public String getAWSSecretKey() {
                return SimpleDbConfig.getInstance().getSecretKey();
            }
        });

        return new SimpleDbRepositoryFactory(sdb, SimpleDbConfig.getInstance());
    }
}
