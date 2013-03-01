package org.springframework.data.simpledb.query.parser;

import org.springframework.data.simpledb.query.RegexpUtils;
import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * Acts as a Parser for Building the Custom Query based on the given
 * {@link Query} parameters<br/>
 * <br/>
 */
public final class QueryParserUtils {
	
	private QueryParserUtils(){}

	public static String buildQueryFromQueryParameters(String valueParameter, String[] rawSelectParameters, String[] rawWhereParameters,
			Class<?> domainClass) {
		if (StringUtils.hasText(valueParameter)) {
			return valueParameter;
		}

		assertParametersInQuery(rawSelectParameters, rawWhereParameters, domainClass);

		StringBuilder stringBuilder = new StringBuilder();
		
		appendSelectClause(stringBuilder, rawSelectParameters, domainClass);
		appendFromClause(stringBuilder, domainClass);
		appendWhereClauseIfPresent(stringBuilder, rawWhereParameters, domainClass);

		return stringBuilder.toString();
	}

	private static void appendWhereClauseIfPresent(StringBuilder stringBuilder, String[] rawWhereParameters, Class<?> domainClass) {
		if (StringUtils.hasText(rawWhereParameters[0])) {
			stringBuilder.append(createQueryClause(" where ", PatternConstants.WHERE_PATTERN, domainClass, rawWhereParameters, " and "));
		}
	}

	private static void appendFromClause(StringBuilder stringBuilder, Class<?> domainClass) {
		stringBuilder.append(" from `" + MetadataParser.getDomain(domainClass) + "`");
	}

	private static void appendSelectClause(StringBuilder stringBuilder, String[] rawSelectParameters, Class<?> domainClass) {
		if (StringUtils.hasText(rawSelectParameters[0])) {
			stringBuilder.append(createQueryClause("select ", PatternConstants.SELECT_PATTERN, domainClass, rawSelectParameters, ", "));
		} else {
			stringBuilder.append("select *");
		}
	}
	
	private static void assertParametersInQuery(String[] rawSelectExpresions, String[] rawWhereExpresions, Class<?> domainClass){
		List<String> whereFieldNames = Collections.emptyList();
		List<String> selectFieldNames = Collections.emptyList();
		if (StringUtils.hasText(rawWhereExpresions[0])) {
			whereFieldNames = RegexpUtils.createFieldNameList(PatternConstants.WHERE_PATTERN.getPattternString(), rawWhereExpresions);
			ReflectionUtils.assertThatFieldsDeclaredInClass(whereFieldNames, domainClass);
		}
		
		if (StringUtils.hasText(rawSelectExpresions[0])) {
			selectFieldNames = RegexpUtils.createFieldNameList(PatternConstants.SELECT_PATTERN.getPattternString(), rawSelectExpresions);
			ReflectionUtils.assertThatFieldsDeclaredInClass(selectFieldNames, domainClass);
		}
		
		assertMatchingSelectAndWhereClauses(selectFieldNames, whereFieldNames);	
				
	}

	private static void assertMatchingSelectAndWhereClauses(List<String> selectFieldNames, List<String> whereFieldNames) {
		if (selectFieldNames.isEmpty()){
			return;
		}
		for (String whereFieldName : whereFieldNames) {
			if (!selectFieldNames.contains(whereFieldName)) {
				throw new IllegalArgumentException("Select clause does not contain parameter from where clause:" + whereFieldName);
			}
		}
		
	}

	public static String createQueryClause(String clause, PatternConstants queryPattern, Class<?> domainClazz, String[] rawParameters, String delimiter) {
		StringBuilder query = new StringBuilder(clause);
		Field idField = MetadataParser.getIdField(domainClazz);
		int idx = 1;

		for (String rawParameter : rawParameters) {
			String replacedParameter = RegexpUtils.convertToSimpleDbExpression(queryPattern, rawParameter, idField);
			query.append(replacedParameter);

			if (idx++ != rawParameters.length) {
				query.append(delimiter);
			}
		}

		return query.toString();
	}

}