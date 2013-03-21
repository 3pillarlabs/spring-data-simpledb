package org.springframework.data.simpledb.query;

import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.core.SimpleDbOperations;

import java.lang.reflect.Method;

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
		SimpleDbQueryMethod queryMethod;
		
		if(SimpleDbQueryMethod.isAnnotatedQuery(method)) {
			queryMethod = new SimpleDbQueryMethod(method, metadata, simpleDbOperations.getSimpleDb()
					.getSimpleDbDomain());
		} else {
			queryMethod = new SimpleDbPartTreeQueryMethod(method, metadata, simpleDbOperations.getSimpleDb()
					.getSimpleDbDomain());
		}
		
		return SimpleDbRepositoryQuery.fromQueryAnnotation(queryMethod, simpleDbOperations);
	}

	public static QueryLookupStrategy create(SimpleDbOperations simpleDbOperations, QueryLookupStrategy.Key key) {
		// TODO check in Spring data core key switching and their semantics (look in spring-data-jpa)
		return new SimpleDbQueryLookupStrategy(simpleDbOperations);
	}
}
