package org.springframework.data.simpledb.query.executions;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.Assert;

public class SingleResultExecution extends AbstractSimpleDbQueryExecution {

	/**
	 * The following single result types can be requested: <br/>
	 * <ul>
	 * <li>SINGLE_FIELD_RESULT - Any Core Type or Primitive Field</li> as returned type for query:
	 * 
	 * <pre>
	 * {@code SELECT field FROM entity}
	 * </pre>
	 * 
	 * <li>COUNT_RESULT - Boxed Long or Primitive long Field</li> as returned type for query:
	 * 
	 * <pre>
	 * {@code SELECT count(*) FROM entity}
	 * </pre>
	 * 
	 * <li>ENTITY_RESULT - Entity</li> as returned type for query:
	 * 
	 * <pre>
	 * {@code SELECT * FROM entity where itemName()="1"}
	 * </pre>
	 * 
	 * </ul>
	 */
	public enum SingleResultType {

		SINGLE_FIELD_RESULT, COUNT_RESULT, ENTITY_RESULT;
	}

	public SingleResultExecution(SimpleDbOperations simpleDbOperations) {
		super(simpleDbOperations);
	}

	@Override
	protected Object doExecute(SimpleDbQueryMethod queryMethod, SimpleDbQueryRunner queryRunner) {

		SingleResultType resultType = detectResultType(queryMethod);

		switch(resultType) {
			case COUNT_RESULT: {
				Class<?> methodReturnedType = queryMethod.getReturnedObjectType();
				boolean isLongClass = Long.class.isAssignableFrom(methodReturnedType);
				boolean islongClass = long.class.isAssignableFrom(methodReturnedType);
				Assert.isTrue(isLongClass || islongClass,
						"Method declared in repository should return type long or Long");
				return queryRunner.executeCount();
			}

			case SINGLE_FIELD_RESULT: {
				String attributeName = queryRunner.getSingleQueryFieldName();
				Object returnedEntity = queryRunner.executeSingleResultQuery();
				return ReflectionUtils.callGetter(returnedEntity, attributeName);
			}

			case ENTITY_RESULT: {
				return queryRunner.executeSingleResultQuery();
			}

			default:
				throw new IllegalArgumentException("Unrecognized Single result type");
		}
	}

	private SingleResultType detectResultType(SimpleDbQueryMethod method) {
		String query = method.getAnnotatedQuery();

		if(QueryUtils.isCountQuery(query)) {
			return SingleResultType.COUNT_RESULT;
		} else if(method.isQueryForEntity()) {
			return SingleResultType.ENTITY_RESULT;
		} else if(QueryUtils.getQueryPartialFieldNames(query).size() == 1) {
			return SingleResultType.SINGLE_FIELD_RESULT;
		} else {
			throw new IllegalArgumentException("Wrong return type for query: " + query);
		}
	}
}