package org.springframework.data.simpledb.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.executions.*;
import org.springframework.data.simpledb.util.FieldType;
import org.springframework.data.simpledb.util.FieldTypeIdentifier;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * {@link RepositoryQuery} implementation that inspects a {@link SimpleDbQueryMethod} for the existence of an
 * {@link org.springframework.data.simpledb.annotation.Query} annotation and provides implementations based on query
 * method information.
 */
public class SimpleDbRepositoryQuery implements RepositoryQuery {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbRepositoryQuery.class);
	private final SimpleDbQueryMethod method;
	private final SimpleDbOperations<?, Serializable> simpledbOperations;

	public SimpleDbRepositoryQuery(SimpleDbQueryMethod method, SimpleDbOperations<?, Serializable> simpledbOperations) {
		this.method = method;
		this.simpledbOperations = simpledbOperations;
	}

	@Override
	public Object execute(Object[] parameters) {
		return getExecution().execute(this, parameters);
	}

	@Override
	public QueryMethod getQueryMethod() {
		return method;
	}

	public Parameters getQueryParameters() {
		return method.getParameters();
	}

	/**
	 * Creates a {@link RepositoryQuery} from the given {@link org.springframework.data.repository.query.QueryMethod}
	 * that is potentially annotated with {@link org.springframework.data.simpledb.annotation.Query}.
	 * 
	 * @param queryMethod
	 * @return the {@link RepositoryQuery} derived from the annotation or {@code null} if no annotation found.
	 */
	public static RepositoryQuery fromQueryAnnotation(SimpleDbQueryMethod queryMethod,
			SimpleDbOperations<?, Serializable> simpleDbOperations) {
		LOGGER.debug("Looking up query for method {}", queryMethod.getName());
		return queryMethod.getAnnotatedQuery() == null ? null : new SimpleDbRepositoryQuery(queryMethod,
				simpleDbOperations);
	}

	public String getAnnotatedQuery() {
		return method.getAnnotatedQuery();
	}

	protected AbstractSimpleDbQueryExecution getExecution() {
		String query = method.getAnnotatedQuery();
		assertNotHavingNestedQueryParameters(query);

		if(method.isPagedQuery()) {
			/*
			 * Paged query must be checked first because the checking is done based on parameter types in the query
			 * method's signature, while the rest of the checks are based on the method's return type
			 */

			return new PagedResultExecution(simpledbOperations);
		}
		if(method.isCollectionQuery()) {
			return new MultipleResultExecution(simpledbOperations);
		} else if(method.isModifyingQuery()) {
			throw new IllegalArgumentException(
					"Modifying query not supported. Please use repository methods for update operations.");
		} else {
			return new SingleResultExecution(simpledbOperations);
		}
	}

	void assertNotHavingNestedQueryParameters(String query) {
		List<String> attributesFromQuery = QueryUtils.getQueryPartialFieldNames(query);
		final Class<?> domainClass = method.getDomainClazz();
		for(String attribute : attributesFromQuery) {
			try {
				Field field = domainClass.getDeclaredField(attribute);
				if(FieldTypeIdentifier.isOfType(field, FieldType.NESTED_ENTITY)) {
					throw new IllegalArgumentException("Invalid query parameter :" + attribute + " is nested object");
				}
			} catch(NoSuchFieldException e) {
				// might be a count or something else
			}
		}
	}
}
