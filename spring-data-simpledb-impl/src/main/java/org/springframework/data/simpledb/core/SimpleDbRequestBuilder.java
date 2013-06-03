package org.springframework.data.simpledb.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.simpledb.util.MapUtils;

import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

/**
 * Taking into account SimpleDb limitations, constructs requests that comply.
 */
public final class SimpleDbRequestBuilder {

	private SimpleDbRequestBuilder() {
		// utility class
	}

	private static final int MAX_NUMBER_OF_ATTRIBUTES_PER_SIMPLE_DB_REQUEST = 256;

	public static List<PutAttributesRequest> createPutAttributesRequests(String domain, String itemName,
			Map<String, List<String>> rawAttributes) {
		List<PutAttributesRequest> putAttributesRequests = new LinkedList<PutAttributesRequest>();

		List<Map<String, List<String>>> attributeChunks = MapUtils.splitToChunksOfSize(rawAttributes,
				MAX_NUMBER_OF_ATTRIBUTES_PER_SIMPLE_DB_REQUEST);

		for(Map<String, List<String>> chunk : attributeChunks) {
			PutAttributesRequest request = createPutAttributesRequest(domain, itemName, chunk);
			putAttributesRequests.add(request);
		}

		return putAttributesRequests;
	}

	private static PutAttributesRequest createPutAttributesRequest(String domain, String itemName,
			Map<String, List<String>> chunk) {
		final PutAttributesRequest putRequest = new PutAttributesRequest();
		putRequest.setDomainName(domain);
		putRequest.setItemName(itemName);

		List<ReplaceableAttribute> simpleDbAttributes = toReplaceableAttributeList(chunk);
		putRequest.setAttributes(simpleDbAttributes);
		return putRequest;
	}

	private static List<ReplaceableAttribute> toReplaceableAttributeList(Map<String, List<String>> attributes) {
		boolean replace = true;
		final List<ReplaceableAttribute> result = new ArrayList<ReplaceableAttribute>();
		
		for(final Map.Entry<String, List<String>> entry : attributes.entrySet()) {
			replace = true;
			for (final String value : entry.getValue()) {
				result.add(new ReplaceableAttribute(entry.getKey(), value, replace));
				replace = false;
			}
		}

		return result;
	}

}
