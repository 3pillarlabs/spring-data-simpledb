package org.springframework.data.simpledb.query.executions;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.data.simpledb.query.SimpleDbQueryMethod;
import org.springframework.data.simpledb.query.SimpleDbQueryRunner;
import org.springframework.data.simpledb.query.SimpleDbRepositoryQuery;
import org.springframework.data.simpledb.query.SimpleDbResultConverter;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.Assert;

/**
 * Factory class for creating the appropriate type of execution.
 */
public class MultipleResultExecution extends AbstractSimpleDbQueryExecution {

    public MultipleResultExecution(SimpleDbOperations<?, Serializable> simpledbOperations) {
        super(simpledbOperations);
    }
    
    /**
     * The following multiple result types can be requested: <br/>
     * <ul>
     *     <li>COLLECTION_OF_DOMAIN_ENTITIES - List&lt;Entity&gt; <br/> as returned type for query  <code> select * from entity</code> </li>
     *     <li>LIST_OF_LIST_OF_OBJECT - List&lt; List &lt; Object &gt; &gt;</li> <br/> as returned type for query <code> select aField, bField from entity</code>
     *     <li>FIELD_OF_TYPE_COLLECTION - Collection &lt; ? &gt; </li> <br/> as returned type for query <code> select collectionField from entity where itemName()="1"</code>
     *     <li>LIST_OF_FIELDS - List &lt; ? &gt; </li> <br/> as returned type for query <code> select aField from entity</code>
     *     <li>SET_OF_FIELDS - Set &lt; ? &gt; </li> <br/> as returned type for query <code> select aField from entity</code>
     * </ul>
     */
    public enum MultipleResultType {
        COLLECTION_OF_DOMAIN_ENTITIES,
        LIST_OF_LIST_OF_OBJECT,
        FIELD_OF_TYPE_COLLECTION,
        LIST_OF_FIELDS,
        SET_OF_FIELDS;

    }

    @Override
    protected Object doExecute(SimpleDbRepositoryQuery query, SimpleDbQueryRunner queryRunner) {
        String queryString = query.getAnnotatedQuery();
        SimpleDbQueryMethod method = (SimpleDbQueryMethod)query.getQueryMethod();

        MultipleResultType resultType = detectResultType(queryString, method);
        switch (resultType){
            case COLLECTION_OF_DOMAIN_ENTITIES:
                final Class<?> returnedClass = query.getQueryMethod().getReturnedObjectType();
                final Class<?> domainClass = ((SimpleDbQueryMethod) query.getQueryMethod()).getDomainClazz();
                Assert.isAssignable(domainClass, returnedClass);
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

    MultipleResultType detectResultType(String query, SimpleDbQueryMethod method) {
        if (isCollectionOfDomainClass(method)) {
            return MultipleResultType.COLLECTION_OF_DOMAIN_ENTITIES;
        } else if (QueryUtils.getQueryPartialFieldNames(query).size() > 1) {
            return MultipleResultType.LIST_OF_LIST_OF_OBJECT;
        } else {
            if (isListOfListOfObject(method)) {
                return MultipleResultType.LIST_OF_LIST_OF_OBJECT;
            } else if (isFieldOfTypeCollection(query, method)) {
                return MultipleResultType.FIELD_OF_TYPE_COLLECTION;
            } else if (List.class.isAssignableFrom(method.getReturnType())) {
                return MultipleResultType.LIST_OF_FIELDS;
            } else if (Set.class.isAssignableFrom(method.getReturnType())) {
                return MultipleResultType.SET_OF_FIELDS;
            } else {
                throw new IllegalArgumentException("Wrong return type for query: " + query);
            }
        }
    }



    private boolean isFieldOfTypeCollection(String query, SimpleDbQueryMethod method) {
        final Class<?> domainClass = method.getDomainClazz();
        List<String> attributesFromQuery = QueryUtils.getQueryPartialFieldNames(query);
        Assert.isTrue(attributesFromQuery.size() == 1, "Query doesn't contain only one attribute in selected clause :" + query);
        String attributeName = attributesFromQuery.get(0);
        try {
            Field field = domainClass.getDeclaredField(attributeName);
            Class<?> type = field.getType();
            if (Collection.class.isAssignableFrom(type)) {
                ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
                Type returnedGenericType = returnType.getActualTypeArguments()[0];
                if (!(returnedGenericType instanceof ParameterizedType)) {
                    return true;
                }
            }
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Filed doesn't exist in entity :" + query, e);
        }
        return false;
    }

    private boolean isListOfListOfObject(SimpleDbQueryMethod method) {
        Type returnedGenericType = getCollectionGenericType(method);
        if (returnedGenericType instanceof ParameterizedType) {
            ParameterizedType secondGenericType = (ParameterizedType) returnedGenericType;
            Class<?> rowType = (Class<?>) secondGenericType.getRawType();
            if (!List.class.isAssignableFrom(rowType)) {
                return false;
            }
            Class<?> genericObject = (Class<?>) secondGenericType.getActualTypeArguments()[0];

            if (genericObject.equals(Object.class)) {
                return true;
            }
        }
        return false;
    }

    private Type getCollectionGenericType(SimpleDbQueryMethod method){
        ParameterizedType returnType =  (ParameterizedType)method.getGenericReturnType();
        return returnType.getActualTypeArguments()[0];
    }

    private boolean isCollectionOfDomainClass(SimpleDbQueryMethod method) {
        Type returnedGenericType = getCollectionGenericType(method);
        return returnedGenericType.equals(method.getDomainClazz());
    }

}
