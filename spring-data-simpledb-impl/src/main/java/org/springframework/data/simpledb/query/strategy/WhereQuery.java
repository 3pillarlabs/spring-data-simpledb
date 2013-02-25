package org.springframework.data.simpledb.query.strategy;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.util.Assert;

public class WhereQuery extends AbstractQueryStrategy{
    private final String wherePattern = "(?:\\s*)(.+?)(?:\\s*)(=|!=|>|<|\\slike|\\snot|\\sbetween\\sin|\\sis|\\severy())";
    private String[] parameters;
    private Class<?> domainClass;

    public WhereQuery(String[] rawWhereParameters, Class<?> domainClass) {
		this.parameters = rawWhereParameters;
        this.domainClass = domainClass;
        super.map = QueryUtils.createFieldNameWithRawParameter(wherePattern, rawWhereParameters);
    }

 public String createWhereClause() {
	 assertThatFieldsDeclaredInClass(domainClass);
	 return createQueryClause(" where ", wherePattern, domainClass, parameters, " and ");
}

	@Override
	String replaceField(String fieldName, String rawParameter, Field idField) {
		final Pattern regex = Pattern.compile(wherePattern);
		final Matcher matcher = regex.matcher(rawParameter);

		if (matcher.find()) {
			String operation = matcher.group(2);
			if (idField != null && fieldName.equals(idField.getName())) {
				return matcher.replaceFirst("itemName()" + operation);
			} else {
				return matcher.replaceFirst("`" + fieldName + "`" + operation);
			}
		}
		Assert.isTrue(false, "wrong parameter in where clause : " + rawParameter);
		return null;
	}
    
    void assertWhereFieldsAreInSelectClause(SelectQuery selectQuery) {
    	if(selectQuery != null){
    		Map<String, String> selectMap = selectQuery.getMap();
    		
			for (Map.Entry<String, String>  whereEntry : map.entrySet()) {
				Assert.isTrue(selectMap.containsKey(whereEntry.getKey()), "select clause should contain all parameters from where clause");
			}
    	}
	}
    
}