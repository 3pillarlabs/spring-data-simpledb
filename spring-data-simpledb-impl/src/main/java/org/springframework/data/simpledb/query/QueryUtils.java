package org.springframework.data.simpledb.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.data.simpledb.util.ReflectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

// TODO: 
// 1. check if where clause has parameters && select has parameters && select does not have "*"
// 	  then if those parameters don't exist in the select, throw an assertion error;
// 2. select -
// 3. refactoring, make some shared methods reusable between select and where features

public final class QueryUtils {

    private static final String BIND_PARAMETER_REGEX = "(\\?)";
    private static final String SINGLE_QUOTE = "'";
    private static final String WHERE_REGEX_PATTERN = "(?:\\s*)(.+?)(?:\\s*)(=|!=|>|<|\\slike|\\snot|\\sbetween\\sin|\\sis|\\severy())";
    private static final String SELECT_REGEX_PATTERN = "(?:\\s*)(.+)(?:\\s*)";

    private QueryUtils() {
    }

    public static String bindQueryParameters(SimpleDbRepositoryQuery query, String... parameterValues) {
        final String rawQuery = query.getAnnotatedQuery();
        String completedQuery = null;

        validateBindParameters(query.getQueryMethod().getParameters(), parameterValues);

        if (hasNamedParameter(query)) {
            completedQuery = bindNamedParameters(query, parameterValues);

        } else if (hasBindParameter(rawQuery)) {
            completedQuery = bindIndexPositionParameters(rawQuery, parameterValues);

        } else {
            completedQuery = rawQuery;
        }

        return completedQuery;
    }

    public static boolean hasNamedParameter(SimpleDbRepositoryQuery query) {
        for (Parameter param : query.getQueryMethod().getParameters()) {
            if (param.isNamedParameter()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public static boolean hasBindParameter(String query) {
        final Pattern regexPattern = Pattern.compile(BIND_PARAMETER_REGEX);
        final Matcher matcher = regexPattern.matcher(query);
        return matcher.find();
    }

    public static String bindNamedParameters(SimpleDbRepositoryQuery query, String... parameterValues) {
        final Parameters parameters = query.getQueryMethod().getParameters();
        final String rawQuery = query.getAnnotatedQuery();

        return buildQueryConditionsWithParameters(rawQuery, parameters, parameterValues);
    }

    public static String buildQueryConditionsWithParameters(String rawQuery, Parameters params, String... parameterValues) {

        Map<String, String> parameterPlaceholderValues = buildPlaceholderValues(params, parameterValues);

        return replaceParameterHolders(rawQuery, parameterPlaceholderValues);
    }

    public static String bindIndexPositionParameters(String queryString, String... values) {

        final Pattern pattern = Pattern.compile(BIND_PARAMETER_REGEX);
        final StringBuilder builder = new StringBuilder();

        final List<String> dividedQuery = Arrays.asList(queryString.split(pattern.toString()));
        int idx = 0;

        Assert.isTrue(dividedQuery.size() == values.length, "Number of binding parameters in method must match number of query binding parameters");

        try {
            for (Iterator<String> iterator = dividedQuery.iterator(); iterator.hasNext(); ++idx) {
                builder.append(iterator.next()).append(SINGLE_QUOTE).append(values[idx]).append(SINGLE_QUOTE);
            }

        } catch (RuntimeException e) {
            throw new MappingException("Invalid Query! Number of binding parameters in method must match number of query binding parameters", e);
        }

        return builder.toString();
    }

    private static String replaceParameterHolders(String rawQuery, Map<String, String> parameterPlaceholderValues) {
        final StringBuilder completedQueryBuilder = new StringBuilder();

        for (int idx = 0; idx < rawQuery.length(); ++idx) {
            String parameterPlaceholder = null;

            if (rawQuery.charAt(idx) == ':') {
                parameterPlaceholder = readUntilChar(rawQuery, idx, ' ');
            }

            if (parameterPlaceholderValues.containsKey(parameterPlaceholder)) {

                completedQueryBuilder.append(SINGLE_QUOTE).append(parameterPlaceholderValues.get(parameterPlaceholder)).append(SINGLE_QUOTE + " ");
                idx += parameterPlaceholder.length();

            } else {
                completedQueryBuilder.append(rawQuery.charAt(idx));
            }
        }

        return completedQueryBuilder.toString().trim();
    }

    private static Map<String, String> buildPlaceholderValues(Parameters parameters, String... parameterValues) {
        Map<String, String> map = new LinkedHashMap<>();

        for (Iterator<Parameter> iterator = parameters.iterator(); iterator.hasNext(); ) {
            Parameter eachParameter = iterator.next();
            map.put(eachParameter.getPlaceholder(), parameterValues[eachParameter.getIndex()]);
        }
        return map;
    }

    private static String readUntilChar(String rawQuery, int idx, char endChar) {
        StringBuilder buffer = new StringBuilder();

        try {
            for (int i = rawQuery.indexOf(rawQuery.charAt(idx), idx); i <= rawQuery.length() && rawQuery.charAt(i) != endChar; ++i) {
                buffer.append(rawQuery.charAt(i));
            }
        } catch (StringIndexOutOfBoundsException stringOOBound) {
            // Query String is ended
        }
        return buffer.toString();
    }

    public static void validateBindParameters(Parameters parameters, String... parameterValues) {
        int numOfParameters = parameters.getNumberOfParameters();

        if (numOfParameters != parameterValues.length) {
            throw new MappingException("Wrong Number of Parameters to bind in Query! Parameter Values size=" + parameterValues.length + ", Method Bind Parameters size=" + numOfParameters);
        }
    }

    public static List<String> getQueryPartialFieldNames(String query) {
        List<String> result = new ArrayList<>();
        String[] vals = query.split(",|\\s");
        boolean isSelect = false;
        for (String val : vals) {
            String trimVal = val.trim();

            if (trimVal.toLowerCase().contains("from")) {
                break;
            }
            if (isSelect && trimVal.length() > 0) {
                result.add(val.trim());
            }
            if (trimVal.toLowerCase().contains("select")) {
                isSelect = true;
            }
        }
        return result;
    }

    public static boolean isCountQuery(String query) {
        return query.toLowerCase().contains("count(");
    }

    private static String validateAndChangeFieldInParameter(String whereParameter, String idField, Class<?> domainClass){
        //pattern to get the field in where clause
        final Pattern regex = Pattern.compile("(?:\\s*)(.+?)(?:\\s*)(=|!=|>|<|\\slike|\\snot|\\sbetween\\sin|\\sis|\\severy())");
        final Matcher matcher = regex.matcher(whereParameter);
        if (matcher.find()) {
            String fieldName = matcher.group(1);
            boolean isFieldDeclared = ReflectionUtils.isFieldInClass(domainClass, fieldName);
            Assert.isTrue(isFieldDeclared, "no such field in entity class : " + fieldName);
            return replaceField(fieldName, idField, matcher);
        }
        Assert.isTrue(false, "wrong parameter in where clause : " + whereParameter);
        return null;
    }

    private static String replaceField(String fieldName, String idField, Matcher matcher) {
            String operation = matcher.group(2);
            if (fieldName.equals(idField)){
                return matcher.replaceFirst("itemName()"+operation);
            } else {
                return matcher.replaceFirst("`"+fieldName+"`"+operation);
            }
    }

    public static String buildQueryFromQueryParameters(String queryFromValueParameter, String[] queryFromSelectParameters, String[] queryFromWhereParameters, Class<?> domainClass){
        if(StringUtils.hasText(queryFromValueParameter)){
            return queryFromValueParameter;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if(StringUtils.hasText(queryFromSelectParameters[0])){
            stringBuilder.append(createQueryClause(queryFromSelectParameters, domainClass, "select ", ", "));
        } else {
            stringBuilder.append("select * from `"+ MetadataParser.getDomain(domainClass)+"`");
        }

        if(StringUtils.hasText(queryFromWhereParameters[0])){
            stringBuilder.append(createQueryClause(queryFromWhereParameters, domainClass, "where ", " and "));
        }

        return stringBuilder.toString();
    }

    private static String createQueryClause(String[] queryClauseParameters, Class<?> domainClazz, String operationClause, String delimiter){
        StringBuilder query = new StringBuilder(operationClause);
        Field idField = MetadataParser.getIdField(domainClazz);
        int idx = 1;
        
        for (String eachParameter : queryClauseParameters) {
        	eachParameter = validateAndChangeFieldInParameter(eachParameter, idField.getName(), domainClazz);
        	query.append(eachParameter);
        	
        	if(idx++ != queryClauseParameters.length) {
        		query.append(delimiter);
        	}
        }
        
        return query.toString();
    }
}
