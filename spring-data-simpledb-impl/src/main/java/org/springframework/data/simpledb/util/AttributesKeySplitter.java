/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.data.simpledb.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AttributesKeySplitter {

	private AttributesKeySplitter() {
		/* utility class */
	}
	
    public static Map<String, Map<String, List<String>>> splitNestedAttributeKeys(Map<String, List<String>> attributes) {
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

    public static Map<String, List<String>> getSimpleAttributes(Map<String, List<String>> attributes) {

        Map<String, List<String>> primitiveAttributes = new LinkedHashMap<>();

        for (final Map.Entry<String, List<String>> entry : attributes.entrySet()) {
            if (isSimpleKey(entry.getKey())) {
                primitiveAttributes.put(entry.getKey(), entry.getValue());
            }
        }

        return primitiveAttributes;

    }


    public static boolean isSimpleKey(final String key) {
        return !key.contains(".");
    }

}
