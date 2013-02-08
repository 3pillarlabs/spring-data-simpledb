package org.springframework.data.simpledb.util;

import org.springframework.data.mapping.model.MappingException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.util.Assert;

/**
 * TODO: inject ` on parameters and match named params order
 *
 */
public class QueryParametersBinder {

    public static String bindParameters(String query, String... params) {
        if(params.length==0) {
            return query;
        }
        
        final Pattern pattern = Pattern.compile("(\\?|\\:\\w+)");
        final StringBuilder builder = new StringBuilder();

        final List<String> divided = Arrays.asList(query.split(pattern.toString()));
        int idx = 0;

        Assert.isTrue(divided.size() == params.length, "Number of Method-binded-parameters must match Query-Binded-Parameters");

        try {
            for(Iterator<String> iterator = divided.iterator(); iterator.hasNext(); ++idx) {
                builder.append(iterator.next()).append(params[idx]);
            }
        } catch(RuntimeException exception) {
            throw new MappingException("Invalid Query! Number of binded parameters in method must match number of query binded parameters");
        }

        return builder.toString();
    }
}
