package org.springframework.data.simpledb.query.strategy;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.simpledb.query.QueryUtils;

// TODO rename parameters -> expressions
public class SelectQueryParser extends AbstractQueryParser {
	private final String selectPattern = "(?:\\s*)(.+)(?:\\s*)";
	private String[] rawSelectParameters;
	private Class<?> domainClass;

	public SelectQueryParser(String[] parameters, Class<?> domainClass) {
		this.rawSelectParameters = parameters;
		this.domainClass = domainClass;
		fieldNameWithParamHash = QueryUtils.createFieldNameRawParameterExpression(selectPattern, rawSelectParameters);
	}

	// TODO: rename replaceField -> convertToSimpleDbExpression
	@Override
	protected String replaceField(String fieldName, String rawParameter, Field idField) {
		 final Pattern regex = Pattern.compile(selectPattern);
    	 final Matcher matcher = regex.matcher(rawParameter);
    	 
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
							.append(createQueryClause("select ", selectPattern, domainClass,rawSelectParameters,  ", "));

		return builtSelectQuery.toString();
	}
}