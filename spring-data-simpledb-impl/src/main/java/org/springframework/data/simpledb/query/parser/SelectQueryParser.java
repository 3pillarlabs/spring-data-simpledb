package org.springframework.data.simpledb.query.parser;

import org.springframework.data.simpledb.query.RegexpUtils;
import org.springframework.data.simpledb.util.ReflectionUtils;

public class SelectQueryParser {
	private String[] rawSelectExpressions;
	private Class<?> domainClass;

	public SelectQueryParser(String[] parameters, Class<?> domainClass) {
		this.rawSelectExpressions = parameters;
		this.domainClass = domainClass;
		fieldNameWithParamMap = RegexpUtils.createFieldNameRawParameterExpression(PatternConstants.SELECT_PATTERN.getPattternString(), rawSelectExpressions);
	}

	String createSelectClause() {
		//TODO pass the EntrySet as parameter to function from MAP
		ReflectionUtils.assertThatFieldsDeclaredInClass(null, domainClass);
		
		final StringBuilder builtSelectQuery = new StringBuilder()
							.append(createQueryClause("select ", PatternConstants.SELECT_PATTERN.getPattternString(), domainClass,rawSelectExpressions,  ", "));

		return builtSelectQuery.toString();
	}
}