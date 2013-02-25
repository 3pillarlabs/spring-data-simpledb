package org.springframework.data.simpledb.query.strategy;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.simpledb.query.QueryUtils;

public class SelectQuery extends AbstractQueryStrategy {
	private final String selectPattern = "(?:\\s*)(.+)(?:\\s*)";
	private String[] rawSelectParameters;
	private Class<?> domainClass;

	public SelectQuery(String[] parameters, Class<?> domainClass) {
		this.rawSelectParameters = parameters;
		this.domainClass = domainClass;
		super.map = QueryUtils.createFieldNameWithRawParameter(selectPattern, rawSelectParameters);
	}

	@Override
	String replaceField(String fieldName, String rawParameter, Field idField) {
		 final Pattern regex = Pattern.compile(selectPattern);
    	 final Matcher matcher = regex.matcher(rawParameter);
		if (idField != null && fieldName.equals(idField.getName())) {
			return matcher.replaceFirst("itemName()");
		} else {
			return matcher.replaceFirst("`" + fieldName + "`");
		}
	}

	String createSelectClause() {
		assertThatFieldsDeclaredInClass(domainClass);
		final StringBuilder builtSelectQuery = new StringBuilder()
							.append(createQueryClause("select ", selectPattern, domainClass,rawSelectParameters,  ", "));

		return builtSelectQuery.toString();
	}
}