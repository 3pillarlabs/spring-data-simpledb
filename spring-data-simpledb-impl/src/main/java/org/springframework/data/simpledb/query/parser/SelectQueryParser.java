package org.springframework.data.simpledb.query.parser;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.simpledb.query.RegexpUtils;

public class SelectQueryParser extends AbstractQueryParser {
	private String[] rawSelectExpressions;
	private Class<?> domainClass;

	public SelectQueryParser(String[] parameters, Class<?> domainClass) {
		this.rawSelectExpressions = parameters;
		this.domainClass = domainClass;
		fieldNameWithParamMap = RegexpUtils.createFieldNameRawParameterExpression(PatternConstants.SELECT_PATTERN.getPattternString(), rawSelectExpressions);
	}

	@Override
	protected String convertToSimpleDbExpression(String fieldName, String rawSelectExpressions, Field idField) {
		 final Pattern regex = Pattern.compile(PatternConstants.SELECT_PATTERN.getPattternString());
    	 final Matcher matcher = regex.matcher(rawSelectExpressions);
    	 
		if (idField != null && fieldName.equals(idField.getName())) {
			return matcher.replaceFirst("itemName()");
		} else {
			return matcher.replaceFirst("`" + fieldName + "`");
		}
	}

	String createSelectClause() {
		// pass the EntrySet as parameter to function
		assertThatFieldsDeclaredInClass(domainClass);
		
		final StringBuilder builtSelectQuery = new StringBuilder()
							.append(createQueryClause("select ", PatternConstants.SELECT_PATTERN.getPattternString(), domainClass,rawSelectExpressions,  ", "));

		return builtSelectQuery.toString();
	}
}