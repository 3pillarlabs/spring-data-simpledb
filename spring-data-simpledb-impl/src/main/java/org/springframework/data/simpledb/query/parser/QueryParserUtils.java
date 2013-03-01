package org.springframework.data.simpledb.query.parser;

import java.lang.reflect.Field;

import org.springframework.data.simpledb.annotation.Query;
import org.springframework.data.simpledb.query.RegexpUtils;
import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.util.StringUtils;

/**
 * Acts as a Parser for Building the Custom Query based on the given
 * {@link Query} parameters<br/>
 * <br/>
 */
public class QueryParserUtils {
	
	private QueryParserUtils(){}

	public static String buildQueryFromQueryParameters(String valueParameter, String[] rawSelectParameters, String rawWhereParameters,
			Class<?> domainClass) {
		
		if (StringUtils.hasText(valueParameter)) {
			return valueParameter;
		}

		StringBuilder stringBuilder = new StringBuilder();
		
		appendSelectClause(stringBuilder, rawSelectParameters, domainClass);
		appendFromClause(stringBuilder, domainClass);
		appendWhereClauseIfPresent(stringBuilder, rawWhereParameters, domainClass);

		return stringBuilder.toString();
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
	
	private static void appendWhereClauseIfPresent(StringBuilder stringBuilder, String rawWhereParameters, Class<?> domainClass) {
		if (StringUtils.hasText(rawWhereParameters)) {
			stringBuilder.append(" where " + rawWhereParameters);
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

}