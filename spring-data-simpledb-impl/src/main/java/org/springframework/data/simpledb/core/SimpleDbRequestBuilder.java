package org.springframework.data.simpledb.core;

import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

import java.util.*;

public class SimpleDbRequestBuilder {

    static final int MAX_NUMBER_OF_ATTRIBUTES_PER_SIMPLE_DB_REQUEST = 256;

    public static List<PutAttributesRequest> createPutAttributesRequests(String domain, String itemName, Map<String, String> rawAttributes) {
        List<PutAttributesRequest> putAttributesRequests = new LinkedList<>();

        List<Map<String, String>> attributeChunks = splitToSimpleDbSupportedChunks(rawAttributes);

        for (Map<String, String> chunk : attributeChunks) {
            PutAttributesRequest request = createPutAttributesRequest(domain, itemName, chunk);
            putAttributesRequests.add(request);
        }

        return putAttributesRequests;


    }

    private static PutAttributesRequest createPutAttributesRequest(String domain, String itemName, Map<String, String> chunk) {
        final PutAttributesRequest putRequest = new PutAttributesRequest();
        putRequest.setDomainName(domain);
        putRequest.setItemName(itemName);

        List<ReplaceableAttribute> simpleDbAttributes = toReplaceableAttributeList(chunk, false);
        putRequest.setAttributes(simpleDbAttributes);
        return putRequest;

    }

    static List<Map<String, String>> splitToSimpleDbSupportedChunks(Map<String, String> rawAttributes) {
        List<Map<String, String>> attributeChunks = new LinkedList<>();


        Set<Map.Entry<String, String>> rawEntries = rawAttributes.entrySet();

        Map<String, String> currentChunk = new LinkedHashMap<>();
        int rawEntryIndex = 0;
        for (Map.Entry<String, String> rawEntry : rawEntries) {

            if (rawEntryIndex % MAX_NUMBER_OF_ATTRIBUTES_PER_SIMPLE_DB_REQUEST == 0) {
                if (currentChunk.size() > 0) {
                    attributeChunks.add(currentChunk);
                }
                currentChunk = new LinkedHashMap<>();
            }

            currentChunk.put(rawEntry.getKey(), rawEntry.getValue());

            rawEntryIndex++;

            if (rawEntryIndex == rawAttributes.size()) {
                //finished iterating
                attributeChunks.add(currentChunk);
            }
        }

        return attributeChunks;
    }


    private static List<ReplaceableAttribute> toReplaceableAttributeList(Map<String, String> attributes, boolean replace) {
        final List<ReplaceableAttribute> result = new ArrayList<>();

        for (final Map.Entry<String, String> entry : attributes.entrySet()) {
            result.add(new ReplaceableAttribute(entry.getKey(), entry.getValue(), replace));
        }

        return result;
    }


}
