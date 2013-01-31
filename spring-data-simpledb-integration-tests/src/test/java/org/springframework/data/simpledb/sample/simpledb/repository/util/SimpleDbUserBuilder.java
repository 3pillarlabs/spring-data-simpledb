package org.springframework.data.simpledb.sample.simpledb.repository.util;

import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;

import java.util.ArrayList;
import java.util.List;

public class SimpleDbUserBuilder {

    public static SimpleDbUser createUserWithSampleAttributes(String itemName) {
        SimpleDbUser user = new SimpleDbUser();
        {
            user.setItemName(itemName);
            
            user.setPrimitiveField(0.01f);

            user.setCoreField("tes_string$");

            final SimpleDbUser.NestedEntity nestedEntity = new SimpleDbUser.NestedEntity();
            {
            	nestedEntity.setNestedPrimitiveField(11);
            }

            user.setNestedEntity(nestedEntity);


            user.setObjectField("JSON");
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
