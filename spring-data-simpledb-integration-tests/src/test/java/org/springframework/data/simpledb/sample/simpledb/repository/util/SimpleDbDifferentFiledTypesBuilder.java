package org.springframework.data.simpledb.sample.simpledb.repository.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.simpledb.sample.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbDifferentFieldTypes;

public class SimpleDbDifferentFiledTypesBuilder {

    public static SimpleDbDifferentFieldTypes createUserWithSampleAttributes(String itemName) {
        SimpleDbDifferentFieldTypes user = new SimpleDbDifferentFieldTypes();
        {
            user.setItemName(itemName);
            
            user.setPrimitiveField(0.01f);

            user.setCoreField("tes_string$");

            List<Integer> list = Arrays.asList(Integer.valueOf(123), Integer.valueOf(23));
            user.setCoreTypeList(list);

            user.setPrimitiveArrayField(new long[]{1234L});

            List<String> sampleJSONList = new LinkedList<>();
            sampleJSONList.add("JSON");

            JSONCompatibleClass json = new JSONCompatibleClass();
            json.setName("Test");
            user.setJsonCompatibleClass(json);

            Set<String> set = new HashSet<>();
            set.add("first");
            set.add("second");
            user.setCoreTypeSet(set);

            Map<String, String> map = new HashMap<>();
            map.put("1","1");
            map.put("2", "2");
            user.setCoreTypeMap(map);

            user.setJsonCompatibleClass(json);

            user.setObjectField(sampleJSONList);

            user.setObjectList( buildListOfObjects());
        }
        return user;
    }

    private static ArrayList<JSONCompatibleClass> buildListOfObjects() {
        ArrayList<JSONCompatibleClass> listOfObjects = new ArrayList<JSONCompatibleClass>();
        JSONCompatibleClass sampleElement = new JSONCompatibleClass();
        sampleElement.setName("SAMPLE");
        listOfObjects.add(sampleElement);
        return listOfObjects;
    }

    public static List<SimpleDbDifferentFieldTypes> createListOfItems(int length) {
        List<SimpleDbDifferentFieldTypes> list = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            String itemName = "Item_" + i;
            SimpleDbDifferentFieldTypes user = createUserWithSampleAttributes(itemName);
            list.add(user);
        }
        return list;
    }
}
