package org.springframework.data.simpledb.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class SimpleDbAttributeKeySplitter {

    private SimpleDbAttributeKeySplitter(){
        //utility class
    }

    static final String SPLIT_ATTRIBUTE_PREFIX_START = "@";


    public static String convertKey(String rawKey, int chunkIndex){
        return rawKey + SPLIT_ATTRIBUTE_PREFIX_START + chunkIndex;
    }

    public static String getKeyGroupSourceAttributeName(List<String> attributeKeyGroup) {
        String firstAttribute = attributeKeyGroup.get(0);
        return  firstAttribute.substring(0, firstAttribute.indexOf(SPLIT_ATTRIBUTE_PREFIX_START));
    }

    public static List<List<String>> getAttributeKeyGroups(Set<String> attributeKeys) {
        List<List<String>> attributeKeyGroups = new LinkedList<List<String>>();

        for(String attributeKey: attributeKeys){
            if(!attributeKey.contains(SPLIT_ATTRIBUTE_PREFIX_START)) {
                //simple attribute
                List<String> simpleAttributeGroup = new ArrayList<String>();
                simpleAttributeGroup.add(attributeKey);
                attributeKeyGroups.add(simpleAttributeGroup);
            } else {
                //many attributes exist
                String groupPrefix = attributeKey.substring(0, attributeKey.indexOf(SPLIT_ATTRIBUTE_PREFIX_START));
                List<String> attributeKeyGroup = getAttributesStartingWith(groupPrefix, attributeKeys);
                attributeKeyGroups.add(attributeKeyGroup);
            }
        }

        return attributeKeyGroups;
    }


    private static List<String> getAttributesStartingWith(String groupPrefix, Set<String> attributeKeys) {
        List<String> ret = new LinkedList<String>();
        for(String key: attributeKeys){
            if(key.startsWith(groupPrefix)){
                ret.add(key);
            }
        }

        return ret;
    }



}
