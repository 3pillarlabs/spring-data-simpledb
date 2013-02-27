package org.springframework.data.simpledb.query.parser;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.simpledb.query.RegexpUtils;
import org.springframework.util.Assert;

public class WhereQueryParser extends QueryParserUtils{
    private String[] parameters;
    private Class<?> domainClass;

    public WhereQueryParser(String[] rawWhereParameters, Class<?> domainClass) {
		this.parameters = rawWhereParameters;
        this.domainClass = domainClass;
        fieldNameWithParamMap = RegexpUtils.createFieldNameRawParameterExpression(PatternConstants.WHERE_PATTERN.getPattternString(), rawWhereParameters);
    }

 public String createWhereClause() {
	 assertThatFieldsDeclaredInClass(domainClass);
	 
	 return createQueryClause(" where ", PatternConstants.WHERE_PATTERN.getPattternString(), domainClass, parameters, " and ");
}

    @Override
    protected void assertWhereFieldsAreInSelectClause(QueryParserUtils queryStrategy) {
    	if(queryStrategy != null){
    		Map<String, String> selectMap = queryStrategy.getFieldNameWithParamMap();
    		
			for (Map.Entry<String, String>  whereEntry : fieldNameWithParamMap.entrySet()) {
				Assert.isTrue(selectMap.containsKey(whereEntry.getKey()), "SELECT clause should contain all parameters from WHERE clause");
			}
    	}
	}
    
}