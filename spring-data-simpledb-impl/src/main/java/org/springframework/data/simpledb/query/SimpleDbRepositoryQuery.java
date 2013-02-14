package org.springframework.data.simpledb.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.query.SimpleDbQueryExecution.*;
import org.springframework.data.simpledb.util.StringUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * {@link RepositoryQuery} implementation that inspects a {@link SimpleDbQueryMethod} for the existence of an {@link org.springframework.data.simpledb.annotation.Query} annotation and provides
 * implementations based on query method information.
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

    public String getAnnotatedQuery() {
        return method.getAnnotatedQuery();
    }

    protected SimpleDbQueryExecution getExecution() {
        //TODO fix this
        String query = method.getAnnotatedQuery();
        if (query.toLowerCase().contains("count(")) {
            return new CountExecution(simpledbOperations);
        } else if (Collection.class.isAssignableFrom(getTypeFieldSelected(query))) {
            return new PartialCollectionFieldExecution(simpledbOperations);
        } else if (method.isCollectionQuery()) {
            if (query.contains("*")) {
                return new CollectionExecution(simpledbOperations);
            } else if (isGenericResultQuery()){
                return new PartialCollectionExecution(simpledbOperations);
            }else if (List.class.isAssignableFrom(method.getReturnType())) {
                return new PartialListOfOneFiledExecution(simpledbOperations);
            } else if (Set.class.isAssignableFrom(method.getReturnType())) {
                return new PartialSetOfOneFiledExecution(simpledbOperations);
            } else {
                throw new IllegalArgumentException("Not implemented");
            }
        } else if (method.isQueryForEntity()) {
            return new SingleResultExecution(simpledbOperations);
        } else if (method.isPageQuery()) {
            throw new IllegalArgumentException("Not implemented");
        } else if (method.isModifyingQuery()) {
            throw new IllegalArgumentException("Not implemented");
        } else {
            return new PartialSingleResultExecution(simpledbOperations);
        }
    }

    /**
     * Creates a {@link RepositoryQuery} from the given {@link org.springframework.data.repository.query.QueryMethod} that is potentially annotated with
     * {@link org.springframework.data.simpledb.annotation.Query}.
     *
     * @param queryMethod
     * @return the {@link RepositoryQuery} derived from the annotation or {@code null} if no annotation found.
     */
    public static RepositoryQuery fromQueryAnnotation(SimpleDbQueryMethod queryMethod, SimpleDbOperations<?, Serializable> simpleDbOperations) {

        LOGGER.debug("Looking up query for method {}", queryMethod.getName());

        return queryMethod.getAnnotatedQuery() == null ? null : new SimpleDbRepositoryQuery(queryMethod, simpleDbOperations);
    }


    private Class<?> getTypeFieldSelected(String query){
        final Class<?> domainClass = method.getDomainClass();
        List<String> attributesFromQuery = StringUtil.getAttributesInQuerry(query);
        String attributeName = attributesFromQuery.get(0);
        Field field = null;
        try {
            field = domainClass.getDeclaredField(attributeName);
            Class<?> type = field.getType();
            return type;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Filed doesn't exist in entity :" + query);
        }
    }

    private boolean isGenericResultQuery(){
        ParameterizedType returnType = method.getGenericReturnType();
        Type genericType = returnType.getActualTypeArguments()[0];

        if(genericType.equals(method.getDomainClass())) {
            return true;
        }

        if (!genericType.equals(Object.class)) {
            return false;
        } else {
        ParameterizedType pp = (ParameterizedType) genericType;
            Class<?> genericObject = (Class<?>) pp.getActualTypeArguments()[0];

            if(genericObject.equals(Object.class)) {
                return true;
            }
        }
        return false;
    }
}
