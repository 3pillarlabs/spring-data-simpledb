package org.springframework.data.simpledb.attributeutil;

import org.springframework.data.simpledb.util.AlphanumStringComparator;

import java.util.*;

/**
 * Used to split, combine attribute values exceeding Simple Db Length limitation: 1024
 */
public final class SimpleDbAttributeValueSplitter {

	private SimpleDbAttributeValueSplitter() {
		// utility class
	}

	public static final int MAX_SIMPLE_DB_ATTRIBUTE_VALUE_LENGTH = 1024;

	public static Map<String, String> splitAttributeValuesWithExceedingLengths(Map<String, String> rawAttributes) {
		Map<String, String> splitAttributes = new LinkedHashMap<String, String>();

		Set<Map.Entry<String, String>> rawEntries = rawAttributes.entrySet();

		for(Map.Entry<String, String> rawEntry : rawEntries) {
			if(rawEntry.getValue().length() > MAX_SIMPLE_DB_ATTRIBUTE_VALUE_LENGTH) {
				splitAttributes.putAll(splitAttributeWithExceedingValue(rawEntry.getKey(), rawEntry.getValue()));
			} else {
				splitAttributes.put(rawEntry.getKey(), rawEntry.getValue());
			}
		}

		return splitAttributes;
	}

	private static Map<String, String> splitAttributeWithExceedingValue(String initialAttributeKey,
			String initialAttributeValue) {
		Map<String, String> splitAttributes = new LinkedHashMap<String, String>();
		List<String> splitValues = splitExceedingValue(initialAttributeValue);
		int i = 0;
		for(String splitValue : splitValues) {
			String splitAttributeKey = SimpleDbAttributeKeySplitter.convertKey(initialAttributeKey, i);
			splitAttributes.put(splitAttributeKey, splitValue);
			i++;
		}

		return splitAttributes;
	}

	private static List<String> splitExceedingValue(String attributeValue) {
		List<String> splitValues = new LinkedList<String>();
		int length = attributeValue.length();
		for(int i = 0; i < length; i += MAX_SIMPLE_DB_ATTRIBUTE_VALUE_LENGTH) {
			splitValues.add(attributeValue.substring(i, Math.min(length, i + MAX_SIMPLE_DB_ATTRIBUTE_VALUE_LENGTH)));
		}
		return splitValues;
	}

	public static Map<String, String> combineAttributeValuesWithExceedingLengths(Map<String, String> splitAttributes) {
		Map<String, String> raw = new LinkedHashMap<String, String>();
		List<List<String>> attributeKeyGroups = SimpleDbAttributeKeySplitter.getAttributeKeyGroups(splitAttributes
				.keySet());
		for(List<String> keyGroup : attributeKeyGroups) {
			if(keyGroup.size() == 1) {
				// was not split
				String key = keyGroup.get(0);
				raw.put(key, splitAttributes.get(key));
			} else {
				raw.put(SimpleDbAttributeKeySplitter.getKeyGroupSourceAttributeName(keyGroup),
						combineAttributeValues(keyGroup, splitAttributes));
			}
		}
		return raw;
	}

	static String combineAttributeValues(List<String> keyGroup, Map<String, String> splitAttributes) {
		Collections.sort(keyGroup, new AlphanumStringComparator());

		StringBuilder builder = new StringBuilder();
		for(String key : keyGroup) {
			builder.append(splitAttributes.get(key));
		}

		return builder.toString();

	}

}
