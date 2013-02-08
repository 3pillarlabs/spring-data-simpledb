package org.springframework.data.simpledb.query;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cclaudiu
 *
 */
public class QueryUtils {

    public static final String BIND_PARAMETER_REGEX = "(\\?)";

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
        final Pattern regexPattern = Pattern.compile("\\?");
        final Matcher matcher = regexPattern.matcher(query);
        return matcher.find();
    }

    public static String bindNamedParameters(SimpleDbRepositoryQuery query, String... values) {
        final Parameters parameters = query.getQueryMethod().getParameters();
        final StringBuilder formattedQuery = new StringBuilder().append(query.getAnnotatedQuery());

        for(Parameter eachParameter : parameters) {
            eachParameter.getPlaceholder();
            int startIndx = formattedQuery.indexOf(eachParameter.getName());
            int endIndx = eachParameter.getName().length();
            formattedQuery.replace(startIndx, endIndx, eachParameter.getPlaceholder());
        }

        return formattedQuery.toString();
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
                builder.append(iterator.next()).append("'").append(values[idx]).append("'");
            }

        } catch(RuntimeException exception) {
            throw new MappingException("Invalid Query! Number of binding parameters in method must match number of query binding parameters");
        }

        return builder.toString();
    }

}
