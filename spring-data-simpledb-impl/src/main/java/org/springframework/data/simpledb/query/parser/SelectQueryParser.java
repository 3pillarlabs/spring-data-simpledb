package org.springframework.data.simpledb.query.parser;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.simpledb.query.RegexpUtils;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class SelectQueryParser {
	private String[] rawSelectExpressions;
	private Class<?> domainClass;

	public SelectQueryParser(String[] parameters, Class<?> domainClass) {
		this.rawSelectExpressions = parameters;
		this.domainClass = domainClass;
		fieldNameWithParamMap = RegexpUtils.createFieldNameRawParameterExpression(PatternConstants.SELECT_PATTERN.getPattternString(), rawSelectExpressions);
	}


	protected String convertToSimpleDbExpression(String fieldName, String rawSelectExpressions, Field idField) {
		 final Pattern regex = Pattern.compile(PatternConstants.SELECT_PATTERN.getPattternString());
    	 final Matcher matcher = regex.matcher(rawSelectExpressions);
    	 
 		if (matcher.find()) {
 			if (idField != null && fieldName.equals(idField.getName())) {
 				return StringUtils.replace(rawSelectExpressions, fieldName, "itemName()");
 			} else {
 				return StringUtils.replace(rawSelectExpressions, fieldName,  "`" + fieldName + "`");
 			}
 		}
	}

	protected String convertToSimpleDbExpression(String fieldName, String rawParameterExpression, Field idField) {
		final Pattern regex = Pattern.compile(PatternConstants.WHERE_PATTERN.getPattternString());
		final Matcher matcher = regex.matcher(rawParameterExpression);

		if (matcher.find()) {
			String operator = matcher.group(2);
			if (idField != null && fieldName.equals(idField.getName())) {
				return matcher.replaceFirst("itemName()" + operator);
			} else {
				return matcher.replaceFirst("`" + fieldName + "`" + operator);
			}
		}
		
		throw new IllegalArgumentException("wrong parameter in where clause : " + rawParameterExpression);
	}

	String createSelectClause() {
		//TODO pass the EntrySet as parameter to function
		ReflectionUtils.assertThatFieldsDeclaredInClass
		assertThatFieldsDeclaredInClass(List<String> fileds, domainClass);
		
		final StringBuilder builtSelectQuery = new StringBuilder()
							.append(createQueryClause("select ", PatternConstants.SELECT_PATTERN.getPattternString(), domainClass,rawSelectExpressions,  ", "));

		return builtSelectQuery.toString();
	}
}