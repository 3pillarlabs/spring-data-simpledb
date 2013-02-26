package org.springframework.data.simpledb.query.parser;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.data.simpledb.query.RegexpUtils;
import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Acts as a Parser for Building the Custom Query based on the given
 * {@link Query} parameters<br/>
 * <br/>
 */
public class QueryParserUtils {

	/**
	 * This hash is used as a shared data structure, holding the fieldName and
	 * it's corresponding rawParameterExpression The use-case of this
	 * Map<FieldName, RawParameter> is for validating that the parameters within
	 * the WHERE clause should appear as parameters also in the SELECT clause It
	 * is represented as a synchronized Map Instance
	 */

	protected Map<String, String> fieldNameWithParamMap;

	public static String buildQueryFromQueryParameters(String valueParameter, String[] rawSelectParameters, String[] rawWhereParameters,
			Class<?> domainClass) {
		if (StringUtils.hasText(valueParameter)) {
			return valueParameter;
		}

		assertMatchingSelectAndWhereClauses(rawSelectParameters, rawWhereParameters);

		StringBuilder stringBuilder = new StringBuilder();
		appendSelectClause(stringBuilder, rawSelectParameters, domainClass);
		appendFromClause(stringBuilder, domainClass);
		appendWhereClauseIfPresent(stringBuilder, rawWhereParameters, domainClass);

		return stringBuilder.toString();
	}

	private static void appendWhereClauseIfPresent(StringBuilder stringBuilder, String[] rawWhereParameters, Class<?> domainClass) {
		if (StringUtils.hasText(rawWhereParameters[0])) {
			WhereQueryParser whereQuery = new WhereQueryParser(rawWhereParameters, domainClass);
			stringBuilder.append(whereQuery.createWhereClause());
		}
	}

	private static void appendFromClause(StringBuilder stringBuilder, Class<?> domainClass) {
		stringBuilder.append(" from `" + MetadataParser.getDomain(domainClass) + "`");
	}

	private static void appendSelectClause(StringBuilder stringBuilder, String[] rawSelectParameters, Class<?> domainClass) {
		SelectQueryParser selectQuery = null;
		if (StringUtils.hasText(rawSelectParameters[0])) {
			selectQuery = new SelectQueryParser(rawSelectParameters, domainClass);
			stringBuilder.append(selectQuery.createSelectClause());
		} else {
			stringBuilder.append("select *");
		}
	}

	private static void assertMatchingSelectAndWhereClauses(String[] rawSelectExpresions, String[] rawWhereExpresions) {
		List<String> selectFieldNames = RegexpUtils.createFieldNameList(PatternConstants.SELECT_PATTERN.getPattternString(), rawSelectExpresions);
		List<String> whereFieldNames = RegexpUtils.createFieldNameList(PatternConstants.WHERE_PATTERN.getPattternString(), rawWhereExpresions);
		for (String whereFieldName : whereFieldNames) {
			if (!selectFieldNames.contains(whereFieldName)) {
				throw new IllegalArgumentException("Select clause does not contain parameter from where clause:" + whereFieldName);
			}
		}
	}

	protected String createQueryClause(String clause, String wherePattern, Class<?> domainClazz, String[] rawParameters, String delimiter) {
		StringBuilder query = new StringBuilder(clause);
		Field idField = MetadataParser.getIdField(domainClazz);
		int idx = 1;

		for (Map.Entry<String, String> eachEntry : fieldNameWithParamMap.entrySet()) {
			String replacedParameter = convertToSimpleDbExpression(eachEntry.getKey(), eachEntry.getValue(), idField);
			query.append(replacedParameter);

			if (idx++ != rawParameters.length) {
				query.append(delimiter);
			}
		}

		return query.toString();
	}



	Map<String, String> getFieldNameWithParamMap() {
		return fieldNameWithParamMap;
	}

}