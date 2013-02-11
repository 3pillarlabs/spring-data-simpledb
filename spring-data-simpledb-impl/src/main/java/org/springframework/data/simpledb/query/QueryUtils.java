package org.springframework.data.simpledb.query;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.simpledb.util.StringUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class QueryUtils {

    private static final String AND = " and ";
    private static final String WHERE = " where ";
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

        final List<String> splittedQueryBasedOnWhere = splitQueryBasedOnPattern(rawQuery, WHERE);

        // ---- maybe the query is not annotated with named parameters at all --- //
        if(splittedQueryBasedOnWhere == null || splittedQueryBasedOnWhere.size() < 2) {
            return rawQuery;
        }

        final String formattedWhereCondition = buildQueryConditionsWithParameters(splittedQueryBasedOnWhere.get(1), parameters, values);
        splittedQueryBasedOnWhere.set(1, formattedWhereCondition);

        return joinQuery(splittedQueryBasedOnWhere);
    }

    public static final List<String> splitQueryBasedOnPattern(String query, String patternToSplit) {
        final Pattern pattern = Pattern.compile(patternToSplit, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);
        List<String> querySplitted = null;

        if(matcher.find()) {
            querySplitted = Arrays.asList(query.split(pattern.toString()));
        }

        return querySplitted;
    }

    public static final String buildQueryConditionsWithParameters(String queryAfterWhere, Parameters params, String... values) {
        final StringBuilder conditionBuilder = new StringBuilder();
        int position = 0;

        for (Iterator<Parameter> paramIterator = params.iterator(); paramIterator.hasNext(); ++position) {
            final Parameter eachParameter = paramIterator.next();
            String formattedCondition = null;

            if(position == 0) {
                conditionBuilder.append(WHERE);
            } else {
                conditionBuilder.append(AND);
            }

            List<String> splittedConditionals = splitQueryBasedOnPattern(queryAfterWhere, AND);

            if(splittedConditionals == null) {
                formattedCondition = StringUtils.replace(queryAfterWhere, eachParameter.getPlaceholder(), SINGLE_QUOTE + values[eachParameter.getIndex()] + SINGLE_QUOTE);
            } else {
                for (String eachConditional : splittedConditionals) {
                    if (!eachConditional.contains(eachParameter.getName())) {
                        continue;
                    }

                    formattedCondition = StringUtils.replace(eachConditional, eachParameter.getPlaceholder(), SINGLE_QUOTE + values[eachParameter.getIndex()] + SINGLE_QUOTE);
                }
            }

            conditionBuilder.append(formattedCondition);
        }

        return conditionBuilder.toString();
    }

    private static String joinQuery(List<String> queryParts) {
        final StringBuilder queryBuilder = new StringBuilder();
        for(Iterator<String> iterator = queryParts.iterator(); iterator.hasNext(); ) {
            queryBuilder.append(iterator.next());
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
}
