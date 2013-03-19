package org.springframework.data.simpledb.query;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

/**
 * Query lookup strategy to execute custom interface query methods <br/>
 * Multiple query lookup strategies can be <b>created and combined</b> here:
 * <ul>
 * <li>create query from method name</li>
 * <li>from custom query annotations</li>
 * </ul>
 * 
 * {@link QueryLookupStrategy} that tries to detect a declared query declared via simple db <b>custom {@link Query}
 * annotation</b>.
 */
public final class SimpleDbQueryLookupStrategy implements QueryLookupStrategy {

	private SimpleDbOperations simpleDbOperations;
	
	private SimpleDbQueryLookupStrategy() {
	}

	public SimpleDbQueryLookupStrategy(SimpleDbOperations simpleDbOperations) {
		this.simpleDbOperations = simpleDbOperations;
	}

	@Override
	public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, NamedQueries namedQueries) {
		SimpleDbQueryMethod queryMethod = new SimpleDbQueryMethod(method, metadata, simpleDbOperations.getSimpleDb()
				.getSimpleDbDomain());

		SimpleDbEntityInformation<?, ?> entityInformation = getEntityInformation(metadata.getDomainType());

		if(queryMethod.isAnnotatedQuery()) {
			return SimpleDbRepositoryQuery.fromQueryAnnotation(queryMethod, simpleDbOperations);
		} else {
			return new PartTreeSimpleDbQuery(queryMethod, simpleDbOperations, entityInformation);
		}
	}

	public <T, ID extends Serializable> SimpleDbEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		String simpleDbDomain = simpleDbOperations.getSimpleDb().getSimpleDbDomain().getDomain(domainClass);
		return (SimpleDbEntityInformation<T, ID>) SimpleDbEntityInformationSupport.getMetadata(domainClass,
				simpleDbDomain);
	}

	public static QueryLookupStrategy create(SimpleDbOperations simpleDbOperations, QueryLookupStrategy.Key key) {
		// TODO check in Spring data core key switching and their semantics (look in spring-data-jpa)
		return new SimpleDbQueryLookupStrategy(simpleDbOperations);
	}
}
