package org.springframework.data.simpledb.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.util.Assert;

/**
 * Constructs additional elements needed by the repository factory i.e. EntityManager for JPA, Some
 * SimpleDbOperationsImpl client class Returns repository creation factory.
 */
public class SimpleDbRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends
		RepositoryFactoryBeanSupport<T, S, ID> {

	private SimpleDbOperations simpleDbOperations;

	@Override
	protected RepositoryFactorySupport createRepositoryFactory() {
		Assert.notNull(simpleDbOperations);

		return new SimpleDbRepositoryFactory(simpleDbOperations);
	}

	/**
	 * Needed by spring data core to inject operations
	 * 
	 * @param simpleDbOperations
	 */
	public void setSimpleDbOperations(SimpleDbOperations simpleDbOperations) {
		this.simpleDbOperations = simpleDbOperations;
	}
}
