package org.springframework.data.simpledb.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.simpledb.query.parser.PatternConstants;

public final class RegexpUtils {
    
	private RegexpUtils() { }
	
    private static final String BACKTICK = "`";

	public static List<String> createFieldNameList(String pattern, String[] rawParameterExpressions){
        final List<String> fieldNameList = new ArrayList<String>();
        
        for (String eachExpression : rawParameterExpressions) {
        	String fieldName = getFieldFromExpresion(pattern, eachExpression);
        	if(fieldName != null){
        		 fieldNameList.add(fieldName);
        	} else {
        		 throw new IllegalArgumentException( "Parameter not found by Matcher: [" + eachExpression + "], Usage: simpleDbField OPERATOR simpleDbValue, eg: field < \"30\"");
        	}
        }
        
        return fieldNameList;
    }
		
    
	public static String convertToSimpleDbExpression(PatternConstants queryPattern, String rawExpression, Field idField) {
		String fieldName = getFieldFromExpresion(queryPattern.getPattternString(), rawExpression);
		
		if(fieldName != null){
			if (idField != null && fieldName.equals(idField.getName())) {
				 return rawExpression.trim().replaceFirst(fieldName, "itemName()");
			} else {
				 return rawExpression.trim().replaceFirst(fieldName, BACKTICK + fieldName + BACKTICK);
			}
		} else {
			throw new IllegalArgumentException("Usage: Wrong Parameter In Query Clause : " + rawExpression + ", select = {\"id\", \"name\"} for SELECT stmt, and where = {\"name\" = 3}" );
		}
		
	}
	
	private static String getFieldFromExpresion(String queryPattern, String rawExpression){
		final Pattern regex = Pattern.compile(queryPattern);
		final Matcher matcher = regex.matcher(rawExpression);

		if (matcher.find()) {
			String fieldName = matcher.group(1);
			return fieldName;
		}

		return null;
	}
}