package org.springframework.data.simpledb.query;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * SimpleDB specific extension of {@link org.springframework.data.repository.query.QueryMethod}. <br/>
 * Adds query extraction based on custom Query annotation and query validation helper methods.
 *
 * @author Oliver Gierke
 */
public class SimpleDbQueryMethod extends QueryMethod {

    //TODO add here custom query extractor
    private final Method method;

    /**
     * Creates a new {@link org.springframework.data.simpledb.query.SimpleDbQueryMethod}
     *
     * @param method must not be {@literal null}
     * @param metadata must not be {@literal null}
     */
    public SimpleDbQueryMethod(Method method, RepositoryMetadata metadata) {
        super(method, metadata);
        this.method = method;

        Assert.isTrue(!(isModifyingQuery() && getParameters().hasSpecialParameter()),
                String.format("Modifying method must not contain %s!", Parameters.TYPES));
        assertParameterNamesInAnnotatedQuery();
    }

    private void assertParameterNamesInAnnotatedQuery() {

        String annotatedQuery = getAnnotatedQuery();

        if (!StringUtils.hasText(annotatedQuery)) {
            return;
        }

        for (Parameter parameter : getParameters()) {

            if (!parameter.isNamedParameter()) {
                continue;
            }

            if (!annotatedQuery.contains(String.format(":%s", parameter.getName()))) {
                throw new IllegalStateException(String.format(
                        "Using named parameters for method %s but parameter '%s' not found in annotated query '%s'!", method,
                        parameter.getName(), annotatedQuery));
            }
        }
    }


    /**
     * Returns the query string declared in a {@link Query} annotation or {@literal null} if neither the annotation found nor the attribute was specified.
     *
     * @return
     */
    public final String getAnnotatedQuery() {
        String queryFromValueParameter = getAnnotationValue("value", String.class);
        String[] queryFromWhereParameter = getAnnotationValue("where", String[].class);
        String[] queryFromSelectParameter = getAnnotationValue("select", String[].class);

        assertCorrectAnnotatedQueryParameters(queryFromValueParameter, queryFromSelectParameter, queryFromWhereParameter);
        return QueryUtils.buildQueryFromQueryParameters(queryFromValueParameter, queryFromSelectParameter, queryFromWhereParameter, getDomainClass());
    }

    private void assertCorrectAnnotatedQueryParameters(String queryFromValueParameter, String[] queryFromSelectParameter, String[] queryFromWhereParameter){
       if(StringUtils.hasText(queryFromValueParameter) && StringUtils.hasText(queryFromSelectParameter[0]) && StringUtils.hasText(queryFromWhereParameter[0])){
           Assert.isTrue(false, "Too many parameters for query. If value parameter present, no select or where parameter");
       }
    }

    /**
     * Returns the {@link Query} annotation's attribute casted to the given type or default value if no annotation available.
     *
     * @param attribute
     * @param type
     * @return
     */
    private <T> T getAnnotationValue(String attribute, Class<T> type) {

        Query annotation = method.getAnnotation(Query.class);
        Object value = annotation == null ? AnnotationUtils.getDefaultValue(Query.class, attribute) : AnnotationUtils
                .getValue(annotation, attribute);

        return type.cast(value);
    }

    public Class<?> getDomainClazz() {
        return super.getDomainClass();
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public boolean returnsFieldOfTypeCollection() {
        String query = getAnnotatedQuery();
        if(!isCollectionQuery()){
            return false;
        }
        List<String> attributesFromQuery = QueryUtils.getQueryPartialFieldNames(query);
        if(attributesFromQuery.size() != 1){
            return false;
        }
        String attributeName = attributesFromQuery.get(0);
        return ReflectionUtils.isOfType(method.getGenericReturnType(), getDomainClass(), attributeName);
    }

    public boolean returnsListOfListOfObject() {
        Type returnedGenericType = getCollectionGenericType();
        return ReflectionUtils.isListOfListOfObject(returnedGenericType);
    }


    public boolean returnsCollectionOfDomainClass() {
        Type returnedGenericType = getCollectionGenericType();
        return returnedGenericType.equals(getDomainClass());
    }

    private Type getCollectionGenericType(){
        Type returnType =  method.getGenericReturnType();
        if(isCollectionQuery()){
            return ((ParameterizedType)returnType).getActualTypeArguments()[0];
        } else {
            return returnType;
        }
    }

}
