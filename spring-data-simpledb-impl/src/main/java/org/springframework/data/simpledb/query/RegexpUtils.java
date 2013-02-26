package org.springframework.data.simpledb.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main Responsibility of RegexpUtils class is to work on regular expressions to build the Strings
 */
public class RegexpUtils {
    
    public static Map<String, String> createFieldNameRawParameterExpression(String pattern, String[] rawParameterExpressions){
        final Pattern regex = Pattern.compile(pattern);
        final Map<String, String> fieldNameWithParamHash = new LinkedHashMap<>();
        
        for (String eachExpression : rawParameterExpressions) {
        	final Matcher rawParameterMatcher = regex.matcher(eachExpression);
	        	 if (rawParameterMatcher.find()) {
	                 String fieldName = rawParameterMatcher.group(1);
	                 fieldNameWithParamHash.put(fieldName, eachExpression);
	        	 } else {
	        		 throw new IllegalArgumentException( "Parameter not found by Matcher: " + eachExpression + ", Matcher: " + rawParameterMatcher.toString());
	        	 }
        }
        
        return Collections.synchronizedMap(fieldNameWithParamHash);
    }
    
    public static List<String> createFieldNameList(String pattern, String[] rawParameterExpressions){
        final Pattern regex = Pattern.compile(pattern);
        final List<String> list = new ArrayList<>();
        
        for (String eachExpression : rawParameterExpressions) {
        	final Matcher rawParameterMatcher = regex.matcher(eachExpression);
	        	 if (rawParameterMatcher.find()) {
	                 String fieldName = rawParameterMatcher.group(1);
	                 list.add(fieldName);
	        	 } else {
	        		 throw new IllegalArgumentException( "Parameter not found by Matcher: " + eachExpression + ", Matcher: " + rawParameterMatcher.toString());
	        	 }
        }
        
        return Collections.synchronizedList(list);
    }
}
