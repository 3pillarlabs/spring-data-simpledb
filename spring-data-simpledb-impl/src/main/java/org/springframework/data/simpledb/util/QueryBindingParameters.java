package org.springframework.data.simpledb.util;

import org.springframework.data.mapping.model.MappingException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author cclaudiu
 *
 */
public class QueryBindingParameters {
    private static final String BIND_QUERY = "SELECT * FROM customer_all where customer_id =  ? and email_id = ? and id=?";
    private static final String NAMED_PARAMS_QUERY = "SELECT * FROM customer_all where customer_id =  :customer_id";

    private static String doProcess(String query, Object... params) {
        final Pattern pattern = Pattern.compile("(\\?|\\:\\w+)");
        final StringBuilder builder = new StringBuilder();

        final List<String> divided = Arrays.asList(query.split(pattern.toString()));
        int idx = 0;

        assert divided.size() != params.length : "Number of Method-binded-parameters must match Query-Binded-Parameters";

        try {
            for(Iterator<String> iterator = divided.iterator(); iterator.hasNext(); ++idx) {
                builder.append(iterator.next()).append(params[idx]);
            }
        } catch(RuntimeException exception) {
            throw new MappingException("Invalid Query! Number of binded parameters in method must match number of query binded parameters");
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(doProcess(BIND_QUERY, "cclaudiu", "cosar", "java"));

        System.out.println(doProcess(NAMED_PARAMS_QUERY, "cosar"));
    }
}
