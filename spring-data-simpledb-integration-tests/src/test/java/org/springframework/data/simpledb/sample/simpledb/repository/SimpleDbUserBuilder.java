package org.springframework.data.simpledb.sample.simpledb.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

public class SimpleDbUserBuilder {

    public static SimpleDbUser createUserWithSampleAttributes(String itemName) {
        SimpleDbUser user = new SimpleDbUser();
        {
            user.setItemName(itemName);
            Map<String, String> attributes;
            user.setAtts(attributes = new HashMap<>());
            {
                attributes.put(itemName + "_Key1", itemName + "_Value1");
                attributes.put(itemName + "_Key2", itemName + "_Value2");
                attributes.put(itemName + "_Key3", itemName + "_Value3");
            }
        }
        return user;
    }

    public static List<SimpleDbUser> createListOfItems(int length) {
        List<SimpleDbUser> list = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            String itemName = "Item_" + i;
            SimpleDbUser user = createUserWithSampleAttributes(itemName);
            list.add(user);
        }
        return list;
    }
}
