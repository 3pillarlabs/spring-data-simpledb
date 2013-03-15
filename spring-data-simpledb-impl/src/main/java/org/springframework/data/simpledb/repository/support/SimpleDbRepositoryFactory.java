package org.springframework.data.simpledb.repository.support;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.core.SimpleDbTemplate;
import org.springframework.data.simpledb.core.domain.DomainManager;
import org.springframework.data.simpledb.query.SimpleDbQueryLookupStrategy;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

/**
 * SimpleDB specific generic repository factory.
 * 
 * See JpaRepositoryFactory
 */
public class SimpleDbRepositoryFactory extends RepositoryFactorySupport {

	private SimpleDbOperations simpleDbOperations;
	private DomainManager domainManager;

	public SimpleDbRepositoryFactory(SimpleDbOperations simpleDbOperations) {
		this.domainManager = new DomainManager(simpleDbOperations.getDB(), simpleDbOperations.getSimpleDb()
				.getDomainManagementPolicy().toString());

		this.simpleDbOperations = new SimpleDbTemplate(simpleDbOperations.getSimpleDb());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.core.support.RepositoryFactorySupport#getTargetRepository(org.springframework
	 * .data.repository.core.RepositoryMetadata)
	 */
	@Override
	protected Object getTargetRepository(RepositoryMetadata metadata) {
		SimpleDbEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

		List<Field> nestedReferences = entityInformation.getReferencedAttributesList(entityInformation.getJavaType());

		for(Field eachNestedReference : nestedReferences) {
			entityInformation.validateReferenceAnnotation(eachNestedReference);
			domainManager.manageDomain(simpleDbOperations.getDomainName(eachNestedReference.getType()));
		}

		domainManager.manageDomain(entityInformation.getDomain());

		SimpleDbRepositoryImpl<?, ?> repo = new SimpleDbRepositoryImpl(entityInformation, simpleDbOperations);

		return repo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.support.RepositoryFactorySupport# getRepositoryBaseClass()
	 */
	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return SimpleDbRepositoryImpl.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.support.RepositoryFactorySupport# getQueryLookupStrategy
	 * (org.springframework.data.repository.query.QueryLookupStrategy.Key)
	 */
	@Override
	protected QueryLookupStrategy getQueryLookupStrategy(QueryLookupStrategy.Key key) {
		return SimpleDbQueryLookupStrategy.create(simpleDbOperations, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.support.RepositoryFactorySupport# getEntityInformation(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T, ID extends Serializable> SimpleDbEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		String simpleDbDomain = simpleDbOperations.getSimpleDb().getSimpleDbDomain().getDomain(domainClass);
		return (SimpleDbEntityInformation<T, ID>) SimpleDbEntityInformationSupport.getMetadata(domainClass,
				simpleDbDomain);
	}
}
