package org.springframework.data.simpledb.attributeutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.simpledb.util.AlphanumStringComparator;

/**
 * Used to split, combine attribute values exceeding Simple Db Length limitation: 1024
 */
public final class SimpleDbAttributeValueSplitter {

	private static Pattern multiValueRexp = Pattern.compile("^\\d+@(.+?)$");
	private static AlphanumStringComparator multiValueComparator = new AlphanumStringComparator();
	
	
	private SimpleDbAttributeValueSplitter() {
		// utility class
	}

	public static final int MAX_ATTR_VALUE_LEN = 1024;

	public static Map<String, List<String>> splitAttributeValuesWithExceedingLengths(Map<String, String> rawAttributes) {
		Map<String, List<String>> splitAttributes = new LinkedHashMap<String, List<String>>();

		Set<Map.Entry<String, String>> rawEntries = rawAttributes.entrySet();

		for(Map.Entry<String, String> rawEntry : rawEntries) {
			splitAttributes.put(rawEntry.getKey(), new ArrayList<String>());
			if(rawEntry.getValue().length() > MAX_ATTR_VALUE_LEN) {
				splitAttributes.get(rawEntry.getKey()).addAll(splitExceedingValue(rawEntry.getValue()));
			} else {
				splitAttributes.get(rawEntry.getKey()).add(rawEntry.getValue());
			}
		}

		return splitAttributes;
	}

	private static List<String> splitExceedingValue(String attributeValue) {
		List<String> splitValues = new LinkedList<String>();
		int length = attributeValue.length(); 
		// calculate number of chunks correcting for added qualifiers
		int numChunks = (length <= MAX_ATTR_VALUE_LEN ? 1 : ((length / MAX_ATTR_VALUE_LEN) + 1));
		int maxChunkLength = MAX_ATTR_VALUE_LEN - String.format("%d@", numChunks).length();
		numChunks = (length <= maxChunkLength ? 1 : ((length / maxChunkLength) + 1));
		int chunkCount = 0;
		for(int i = 0; i < length; i += maxChunkLength) {
			String e = attributeValue.substring(i, Math.min(length, i + maxChunkLength));
			splitValues.add(String.format("%d@%s", chunkCount++, e));
		}
		return splitValues;
	}

	public static Map<String, String> combineAttributeValuesWithExceedingLengths(Map<String, List<String>> multiValueAttributes) {
		final Map<String, String> attributes = new HashMap<String, String>();
		for (Map.Entry<String, List<String>> entry : multiValueAttributes.entrySet()) {
			List<String> values = entry.getValue();
			if (values.size() == 1) {
				attributes.put(entry.getKey(), values.get(0));
			} else {
				Collections.sort(values, multiValueComparator);
				StringBuilder builder = new StringBuilder();
				for (String vwp : values) {
					Matcher m = multiValueRexp.matcher(vwp);
					if (m.find()) {
						builder.append(m.group(1));
					} else {
						throw new DataIntegrityViolationException("Multivalue attribute with digit@ pattern but no following value");
					}
				}
				attributes.put(entry.getKey(), builder.toString());
			}
		}
		
		return attributes;
	}

}
