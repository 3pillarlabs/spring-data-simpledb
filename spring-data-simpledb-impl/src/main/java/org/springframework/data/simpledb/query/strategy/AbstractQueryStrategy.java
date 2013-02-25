package org.springframework.data.simpledb.query.strategy;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 *  Acts as a Strategy for Building the Custom Query based on given parameters
 *  
 *  Query Annotation can have three Types of Parameters
 *  <pre><b>value</b>: if value annotation is present, query string should be correct and well formatted </pre>
 * <pre><b>select</b>: contains selective attributes, can be independent of the "where" clause; the Domain is inferred at runtime based on Repository-Metadata</pre>
 * <pre><b>where</b>: contains filtering attributes, can be independent of the "select" clause; the Domain is inferred at runtime based on Repository-Metadata</pre>
 *
 */
public abstract class AbstractQueryStrategy {
	protected Map<String, String> map;
    
    public static String buildQueryFromQueryParameters(String queryFromValueParameter, String[] rawSelectParameters, String[] rawWhereParameters, Class<?> domainClass){
        if(StringUtils.hasText(queryFromValueParameter)){
            return queryFromValueParameter;
        }

        StringBuilder stringBuilder = new StringBuilder();
        SelectQuery selectQuery = null;
        if(StringUtils.hasText(rawSelectParameters[0])){
            selectQuery = new SelectQuery(rawSelectParameters, domainClass);
            stringBuilder.append(selectQuery.createSelectClause());
        } else {
            stringBuilder.append("select *");
        }
        stringBuilder.append(" from `"+ MetadataParser.getDomain(domainClass)+"`");

        WhereQuery whereQuery = null;
        if(StringUtils.hasText(rawWhereParameters[0])){
            whereQuery = new WhereQuery(rawWhereParameters, domainClass);
            stringBuilder.append(whereQuery.createWhereClause());
            whereQuery.assertWhereFieldsAreInSelectClause(selectQuery);
        }

        return stringBuilder.toString();
    }
   
    public String createQueryClause(String clause, String wherePattern, Class<?> domainClazz, String[] rawParameters, String delimiter) {
        StringBuilder query = new StringBuilder(clause);
        Field idField = MetadataParser.getIdField(domainClazz);
        int idx = 1;
        
        for (Map.Entry<String, String> eachEntry : map.entrySet()) {
            String replacedParameter = replaceField(eachEntry.getKey(), eachEntry.getValue(), idField);
        	query.append(replacedParameter);
        	
        	if(idx++ != rawParameters.length) {
        		query.append(delimiter);
        	}
        }
        
        return query.toString();
    }

	protected void assertThatFieldsDeclaredInClass(Class<?> domainClazz) {
		for (Map.Entry<String, String> eachEntry : map.entrySet()) {
			boolean isFieldDeclared = ReflectionUtils.isFieldInClass(domainClazz, eachEntry.getKey());
			Assert.isTrue(isFieldDeclared, "no such field in entity class : " + eachEntry.getKey());
		}
	}
	
    abstract String replaceField(String fieldName, String rawParameter, Field idField);

	public Map<String, String> getMap() {
		return map;
	}
}