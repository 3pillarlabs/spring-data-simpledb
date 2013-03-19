package org.springframework.data.simpledb.query;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.core.SimpleDbDomain;
import org.springframework.data.simpledb.query.parser.QueryParserUtils;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * SimpleDB specific extension of {@link org.springframework.data.repository.query.QueryMethod}. <br/>
 * Adds query extraction based on custom Query annotation and query validation helper methods.
 * 
 * @author Oliver Gierke
 */
public class SimpleDbQueryMethod extends QueryMethod {

	// TODO add here custom query extractor
	private final Method method;
	private final SimpleDbDomain simpleDbDomain;

	/**
	 * Creates a new {@link org.springframework.data.simpledb.query.SimpleDbQueryMethod}
	 * 
	 * @param method
	 *            must not be {@literal null}
	 * @param metadata
	 *            must not be {@literal null}
	 * @param simpleDbDomain
	 */
	public SimpleDbQueryMethod(Method method, RepositoryMetadata metadata, SimpleDbDomain simpleDbDomain) {
		super(method, metadata);
		this.method = method;
		this.simpleDbDomain = simpleDbDomain;

		Assert.isTrue(!(isModifyingQuery() && getParameters().hasSpecialParameter()),
				String.format("Modifying method must not contain %s!", Parameters.TYPES));
		assertParameterNamesInAnnotatedQuery();
	}

	private void assertParameterNamesInAnnotatedQuery() {

		String annotatedQuery = getAnnotatedQuery();

		if(!StringUtils.hasText(annotatedQuery)) {
			return;
		}

		for(Parameter parameter : getParameters()) {

			if(!parameter.isNamedParameter()) {
				continue;
			}

			if(!annotatedQuery.contains(String.format(":%s", parameter.getName()))) {
				throw new IllegalStateException(String.format(
						"Using named parameters for method %s but parameter '%s' not found in annotated query '%s'!",
						method, parameter.getName(), annotatedQuery));
			}
		}
	}

	/**
	 * Returns the query string declared in a {@link Query} annotation or {@literal null} if neither the annotation
	 * found nor the attribute was specified.
	 * 
	 * @return a Query String
	 */
	public final String getAnnotatedQuery() {
		String valueParameter = getAnnotationValue(Query.QueryClause.VALUE.getQueryClause(), String.class);
		String whereParameters = getAnnotationValue(Query.QueryClause.WHERE.getQueryClause(), String.class);
		String[] selectParameters = getAnnotationValue(Query.QueryClause.SELECT.getQueryClause(), String[].class);

		return QueryParserUtils.buildQueryFromQueryParameters(valueParameter, selectParameters, whereParameters,
				simpleDbDomain.getDomain(getDomainClass()));
	}

	public boolean isAnnotatedQuery() {
		return method.getAnnotation(Query.class) != null;
	}
	
	/**
	 * Returns the {@link Query} annotation's attribute casted to the given type or default value if no annotation
	 * available.
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
		if(!isCollectionQuery()) {
			return false;
		}
		List<String> attributesFromQuery = QueryUtils.getQueryPartialFieldNames(query);
		if(attributesFromQuery.size() != 1) {
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

	private Type getCollectionGenericType() {
		Type returnType = method.getGenericReturnType();
		if(isCollectionQuery()) {
			return ((ParameterizedType) returnType).getActualTypeArguments()[0];
		} else {
			return returnType;
		}
	}

	/**
	 * @return whether or not the query method contains a {@link Pageable} parameter in its signature.
	 */
	public boolean isPagedQuery() {
		boolean isPaged = false;

		final Iterator<Parameter> it = getParameters().iterator();

		while(it.hasNext()) {
			final Parameter param = it.next();

			if(Pageable.class.isAssignableFrom(param.getType())) {
				isPaged = true;
				break;
			}
		}

		return isPaged;
	}

}
