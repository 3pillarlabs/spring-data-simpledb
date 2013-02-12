package org.springframework.data.simpledb.query;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cclaudiu
 *
 */

public final class QueryUtils {

    private static final String BIND_PARAMETER_REGEX = "(\\?)";
    private static final String SINGLE_QUOTE = "'";

    private QueryUtils() {}

    public static String bindQueryParameters(SimpleDbRepositoryQuery query, String... values) {
        final String rawQuery = query.getAnnotatedQuery();
        String returnedQuery = null;

        if(hasNamedParameter(query)) {
            returnedQuery = bindNamedParameters(query, values);

        } else if(hasBindParameter(rawQuery)) {
            returnedQuery = bindIndexPositionParameters(rawQuery, values);

        } else {
            returnedQuery = rawQuery;
        }

        return returnedQuery;
    }

    public static boolean hasNamedParameter(SimpleDbRepositoryQuery query) {
        for(Parameter param : query.getQueryMethod().getParameters()) {
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

    public static final String bindNamedParameters(SimpleDbRepositoryQuery query, String... values) {
        final Parameters parameters = query.getQueryMethod().getParameters();
        final String rawQuery = query.getAnnotatedQuery();

        return buildQueryConditionsWithParameters(rawQuery, parameters, values);
    }

    public static final String buildQueryConditionsWithParameters(String queryAfterWhere, Parameters params, String... values) {

        final StringBuilder queryBuilder = new StringBuilder();
        StringBuilder buffer = new StringBuilder();

        Map<String, Integer> parameters = buildParameters(params);

        for(int idx = 0; idx < queryAfterWhere.length(); ++idx) {

            if(queryAfterWhere.charAt(idx) == ':') {
                try {
                    for(int i = queryAfterWhere.indexOf(queryAfterWhere.charAt(idx), idx); queryAfterWhere.charAt(i) != ' '; ++i) {
                        buffer.append(queryAfterWhere.charAt(i));
                    }
                } catch(StringIndexOutOfBoundsException stringOOBound) {
                      // Query String is ended
                }
            }

            if(parameters.containsKey(buffer.toString())) {

                queryBuilder.append(SINGLE_QUOTE + values[parameters.get(buffer.toString())] + SINGLE_QUOTE + " ");
                // bypass holders
                idx += buffer.toString().length();

                // reset buffer for the next Token Named Parameter if exists
                buffer = new StringBuilder();
            } else {
                queryBuilder.append(queryAfterWhere.charAt(idx));
            }
        }


        return queryBuilder.toString();
    }


    public static String bindIndexPositionParameters(String queryString, String... values) {

        if(values.length==0) {
            return queryString;
        }

        final Pattern pattern = Pattern.compile(BIND_PARAMETER_REGEX);
        final StringBuilder builder = new StringBuilder();

        final List<String> divided = Arrays.asList(queryString.split(pattern.toString()));
        int idx = 0;

        Assert.isTrue(divided.size() == values.length, "Number of binding parameters in method must match number of query binding parameters");

        try {
            for(Iterator<String> iterator = divided.iterator(); iterator.hasNext(); ++idx) {
                builder.append(iterator.next()).append(SINGLE_QUOTE).append(values[idx]).append(SINGLE_QUOTE);
            }

        } catch(RuntimeException exception) {
            throw new MappingException("Invalid Query! Number of binding parameters in method must match number of query binding parameters");
        }

        return builder.toString();
    }

    private static Map<String, Integer> buildParameters(Parameters parameters) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for(Iterator<Parameter> iterator = parameters.iterator(); iterator.hasNext(); ) {
            Parameter eachParameter = iterator.next();
            map.put(eachParameter.getPlaceholder(), Integer.valueOf(eachParameter.getIndex()));
        }
        return map;
    }

}
