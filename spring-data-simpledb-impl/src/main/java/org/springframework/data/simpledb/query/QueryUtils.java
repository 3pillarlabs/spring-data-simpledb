package org.springframework.data.simpledb.query;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;
import org.springframework.data.simpledb.util.SupportedCoreTypes;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main Responsibility of QueryUtils is to work with: <br/>
 * <b>Query Strings</b> <br/>
 * <b>Build Indexed-By Queries</b> <br/>
 * <b>Named Queries</b> <br/>
 */

public final class QueryUtils {

    private static final String BIND_PARAMETER_REGEX = "(\\?)";
    private static final String NAMED_PARAMETER_REGEX = "(\\:)";
    private static final String SINGLE_QUOTE = "'";

    private QueryUtils() {
    }

    public static String bindQueryParameters(SimpleDbQueryMethod queryMethod, Class<?> domainClazz,
                                             Object... parameterValues) {
        final String rawQuery = queryMethod.getAnnotatedQuery();
        String completedQuery = null;

        if(hasNamedParameter(queryMethod)) {
            completedQuery = bindNamedParameters(queryMethod, parameterValues);

        } else if(hasBindParameter(rawQuery)) {
            completedQuery = bindIndexPositionParameters(rawQuery, parameterValues);

        } else {
            completedQuery = rawQuery;
        }

        return completedQuery;
    }

    public static boolean hasNamedParameter(SimpleDbQueryMethod queryMethod) {
        for(Parameter param : queryMethod.getParameters()) {
            if(param.isNamedParameter()) {
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

    public static void validateBindParametersCount(Parameters parameters, Object... parameterValues) {
        int numOfParameters = parameters.getNumberOfParameters();

        if(numOfParameters != parameterValues.length) {
            throw new MappingException("Wrong Number of Parameters to bind in Query! Parameter Values size="
                    + parameterValues.length + ", Method Bind Parameters size=" + numOfParameters);
        }
    }

    /**
     * Supported types: primitives & core java types (Date, primitive arrays, primitive wrappers)
     */
    public static void validateBindParametersTypes(Parameters parameters, Object... parameterValues) {
        final Iterator<Parameter> it = parameters.iterator();

        while(it.hasNext()) {
            final Parameter param = it.next();
            final Class<?> paramType = param.getType();

            if(!(param.isSpecialParameter() || SupportedCoreTypes.isSupported(paramType))) {
                throw (new IllegalArgumentException("Type " + paramType
                        + " not supported as an annotated query parameter!"));
            }
        }
    }

    public static List<String> getQueryPartialFieldNames(String query) {
        List<String> result = new ArrayList<String>();
        String[] vals = query.split(",|\\s");
        boolean isSelect = false;
        for(String val : vals) {
            String trimVal = val.trim();

            if(trimVal.toLowerCase().contains("from")) {
                break;
            }
            if(isSelect && trimVal.length() > 0) {
                result.add(val.trim());
            }
            if(trimVal.toLowerCase().contains("select")) {
                isSelect = true;
            }
        }
        return result;
    }

    public static boolean isCountQuery(String query) {
        return query.toLowerCase().contains("count(");
    }

    static String bindNamedParameters(SimpleDbQueryMethod queryMethod, Object... parameterValues) {
        final Parameters parameters = queryMethod.getParameters();
        final String rawQuery = queryMethod.getAnnotatedQuery();

        return buildQueryConditionsWithParameters(rawQuery, parameters, parameterValues);
    }

    static String buildQueryConditionsWithParameters(String rawQuery, Parameters params, Object... parameterValues) {

        final StringBuilder stringBuilder = new StringBuilder(rawQuery);
        for(Iterator<Parameter> iterator = params.iterator(); iterator.hasNext();) {

            Parameter eachParameter = iterator.next();
            String namedParameter = eachParameter.getPlaceholder();

            int startPointOfParameter = stringBuilder.indexOf(namedParameter);
            Assert.isTrue(startPointOfParameter > -1);

            String beforeNamedParameter = stringBuilder.substring(0, startPointOfParameter);
            String replacedValue;
            if(isInOperation(beforeNamedParameter)){
                replacedValue = createInOperatorStatement(parameterValues[eachParameter.getIndex()]);
            } else {
                replacedValue = createParameterValueWithQuotes(parameterValues[eachParameter.getIndex()]);
            }
            stringBuilder.replace(startPointOfParameter, startPointOfParameter+namedParameter.length(), replacedValue);
        }

        return stringBuilder.toString().trim();
    }

    static String bindIndexPositionParameters(String queryString, Object... values) {

        final Pattern pattern = Pattern.compile(BIND_PARAMETER_REGEX);
        final StringBuilder builder = new StringBuilder();

        final List<String> dividedQuery = Arrays.asList(queryString.trim().split(pattern.toString()));
        int idx = 0;

        try {
            for(Iterator<String> iterator = dividedQuery.iterator(); iterator.hasNext(); ++idx) {
                String partialQuery = iterator.next();
                builder.append(partialQuery);
                if(isInOperation(partialQuery)){
                    builder.append(createInOperatorStatement(values[idx]));
                }else{
                    builder.append(createParameterValueWithQuotes(values[idx]));
                }
            }

        } catch(RuntimeException e) {
            throw new MappingException(
                    "Invalid Query! Number of binding parameters in method must match number of query binding parameters",
                    e);
        }

        return builder.toString();
    }

    private static boolean isInOperation(String queryPart){
        final String trimedString = queryPart.trim().toLowerCase();
        if(trimedString.length()>=2){
            return trimedString.endsWith("in");
        }
        return false;
    }

    private static String createParameterValueWithQuotes(Object parameterValue){
        StringBuilder stringBuilder = new StringBuilder("").append(SINGLE_QUOTE)
                .append(SimpleDBAttributeConverter.encode(parameterValue)).append(SINGLE_QUOTE);
        return stringBuilder.toString();
    }


    private static String createInOperatorStatement(Object parameterValue){
        List<String> encodedArray = SimpleDBAttributeConverter.encodeArray(parameterValue);
        StringBuilder stringBuilder = new StringBuilder("(");
        int numberOfParameters = encodedArray.size()-1;
        for (int i = 0; i < numberOfParameters; i++) {
            stringBuilder.append("'").append(encodedArray.get(i)).append("',");
        }
        stringBuilder.append("'").append(encodedArray.get(numberOfParameters)).append("')");
        return stringBuilder.toString();
    }

    public static String escapeQueryAttributes(String rawQuery, String idFieldName) {
        String escapedQuery = rawQuery.replaceAll("\\s" + idFieldName + "\\s", " itemName() ");

        if(escapedQuery.endsWith(idFieldName)) {
            escapedQuery = escapedQuery.substring(0, escapedQuery.length() - idFieldName.length());
            escapedQuery += "itemName()";
        }

        return escapedQuery;
    }
}
