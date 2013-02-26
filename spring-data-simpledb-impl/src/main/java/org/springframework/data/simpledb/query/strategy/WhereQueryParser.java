package org.springframework.data.simpledb.query.strategy;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.simpledb.query.QueryUtils;
import org.springframework.util.Assert;

public class WhereQueryParser extends AbstractQueryParser{
    private final String wherePattern = "(?:\\s*)(.+?)(?:\\s*)(=|!=|>|<|\\slike|\\snot|\\sbetween\\sin|\\sis|\\severy())";
    private String[] parameters;
    private Class<?> domainClass;

    public WhereQueryParser(String[] rawWhereParameters, Class<?> domainClass) {
		this.parameters = rawWhereParameters;
        this.domainClass = domainClass;
        fieldNameWithParamHash = QueryUtils.createFieldNameRawParameterExpression(wherePattern, rawWhereParameters);
    }

 public String createWhereClause() {
	 assertThatFieldsDeclaredInClass(domainClass);
	 
	 return createQueryClause(" where ", wherePattern, domainClass, parameters, " and ");
}

	@Override
	protected String replaceField(String fieldName, String rawParameterExpression, Field idField) {
		final Pattern regex = Pattern.compile(wherePattern);
		final Matcher matcher = regex.matcher(rawParameterExpression);

		if (matcher.find()) {
			String operator = matcher.group(2);
			if (idField != null && fieldName.equals(idField.getName())) {
				return matcher.replaceFirst("itemName()" + operator);
			} else {
				return matcher.replaceFirst("`" + fieldName + "`" + operator);
			}
		}
		
		// TODO: throw IAE rather than Assert
		Assert.isTrue(false, "wrong parameter in where clause : " + rawParameterExpression);
		return null;
	}
    
    @Override
    protected void assertWhereFieldsAreInSelectClause(AbstractQueryParser queryStrategy) {
    	if(queryStrategy != null){
    		Map<String, String> selectMap = queryStrategy.getFieldNameWithParamHash();
    		
			for (Map.Entry<String, String>  whereEntry : fieldNameWithParamHash.entrySet()) {
				Assert.isTrue(selectMap.containsKey(whereEntry.getKey()), "SELECT clause should contain all parameters from WHERE clause");
			}
    	}
	}
    
}