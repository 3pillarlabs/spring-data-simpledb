/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.data.simpledb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleDBEntityUtil {

    public static boolean isPrimitiveKey(final String key) {
        return !key.contains(".");
    }

    public static Map<String, Map<String, List<String>>> splitNestedAttributeValues(Map<String, List<String>> attributes) {
        final Map<String, Map<String, List<String>>> nestedFieldAttributes = new HashMap<>();
        for (final Map.Entry<String, List<String>> entry : attributes.entrySet()) {
            final String key = entry.getKey();

            if (key.contains(".")) {
                Map<String, List<String>> nestedFieldValues = new HashMap<>();
                int prefixIndex = key.indexOf('.');
                final String nestedFieldName = key.substring(0, prefixIndex);
                final String subField = key.substring(prefixIndex + 1);

                if (nestedFieldAttributes.containsKey(nestedFieldName)) {
                    nestedFieldValues = nestedFieldAttributes.get(nestedFieldName);
                }

                nestedFieldValues.put(subField, entry.getValue());

                nestedFieldAttributes.put(nestedFieldName, nestedFieldValues);
            }
        }
        return nestedFieldAttributes;
    }
}
