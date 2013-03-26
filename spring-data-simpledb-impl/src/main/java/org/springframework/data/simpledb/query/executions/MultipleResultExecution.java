package org.springframework.data.simpledb.query.executions;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbResultConverter;
import org.springframework.data.simpledb.reflection.ReflectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Factory class for creating the appropriate type of execution.
 */
public class MultipleResultExecution extends AbstractSimpleDbQueryExecution {

	public MultipleResultExecution(SimpleDbOperations simpledbOperations) {
		super(simpledbOperations);
	}

	/**
	 * The following multiple result types can be requested: <br/>
	 * <ul>
	 * <li>COLLECTION_OF_DOMAIN_ENTITIES - {@code List<Entity>} <br/>
	 * as returned type for query <code> select * from entity</code></li>
	 * <li>LIST_OF_LIST_OF_OBJECT - {@code List<List<Object>>} <br/>
	 * as returned type for query <code> select aField, bField from entity</code></li>
	 * <li>FIELD_OF_TYPE_COLLECTION - {@code Collection<?>} <br/>
	 * as returned type for query <code> select collectionField from entity where itemName()="1"</code></li>
	 * <li>LIST_OF_FIELDS - {@code List<?>} <br/>
	 * as returned type for query <code> select aField from entity</code></li>
	 * <li>SET_OF_FIELDS - {@code Set<?>} <br/>
	 * as returned type for query <code> select aField from entity</code></li>
	 * </ul>
	 */
	public enum MultipleResultType {
		COLLECTION_OF_DOMAIN_ENTITIES, LIST_OF_LIST_OF_OBJECT, FIELD_OF_TYPE_COLLECTION, LIST_OF_FIELDS, SET_OF_FIELDS;

	}

	@Override
	protected Object doExecute(SimpleDbQueryMethod method, SimpleDbQueryRunner queryRunner) {

		MultipleResultType resultType = detectResultType(method);
		switch(resultType) {
			case COLLECTION_OF_DOMAIN_ENTITIES:
				return queryRunner.executeQuery();
			case LIST_OF_LIST_OF_OBJECT:
				List<?> returnList = queryRunner.executeQuery();
				List<String> requestedQueryFieldNames = queryRunner.getRequestedQueryFieldNames();
				return SimpleDbResultConverter.toListOfListOfObject(returnList, requestedQueryFieldNames);
			case FIELD_OF_TYPE_COLLECTION:
				String attributeName = queryRunner.getSingleQueryFieldName();
				Object returnedEntity = queryRunner.executeSingleResultQuery();
				return ReflectionUtils.callGetter(returnedEntity, attributeName);
			case LIST_OF_FIELDS:
				String attributeName1 = queryRunner.getSingleQueryFieldName();
				List<?> returnListFromDb = queryRunner.executeQuery();
				return SimpleDbResultConverter.filterNamedAttributesAsList(returnListFromDb, attributeName1);
			case SET_OF_FIELDS:
				String attributeName3 = queryRunner.getSingleQueryFieldName();
				List<?> returnListFromDb1 = queryRunner.executeQuery();
				return SimpleDbResultConverter.filterNamedAttributesAsSet(returnListFromDb1, attributeName3);

		}
		throw new IllegalArgumentException("Unrecognized multiple result type");
	}

	MultipleResultType detectResultType(SimpleDbQueryMethod method) {
		String query = method.getAnnotatedQuery();
		if(method.returnsCollectionOfDomainClass()) {
			return MultipleResultType.COLLECTION_OF_DOMAIN_ENTITIES;
		} else if(QueryUtils.getQueryPartialFieldNames(query).size() > 1) {
			return MultipleResultType.LIST_OF_LIST_OF_OBJECT;
		} else {
			if(method.returnsListOfListOfObject()) {
				return MultipleResultType.LIST_OF_LIST_OF_OBJECT;
			} else if(method.returnsFieldOfTypeCollection()) {
				return MultipleResultType.FIELD_OF_TYPE_COLLECTION;
			} else if(List.class.isAssignableFrom(method.getReturnType())) {
				return MultipleResultType.LIST_OF_FIELDS;
			} else if(Set.class.isAssignableFrom(method.getReturnType())) {
				return MultipleResultType.SET_OF_FIELDS;
			} else {
				throw new IllegalArgumentException("Wrong return type for query: " + query);
			}
		}
	}
}
