package org.springframework.data.simpledb.repository.util;

import org.springframework.data.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.domain.SimpleDbUser;

import java.util.*;

public class SimpleDbUserBuilder {

	public static SimpleDbUser createUserWithSampleAttributes(String itemName) {
		SimpleDbUser user = new SimpleDbUser();
		{
			user.setItemName(itemName);

			user.setPrimitiveField(0.01f);

			user.setCoreField("tes_string$");

			List<Integer> list = Arrays.asList(Integer.valueOf(123), Integer.valueOf(23));
			user.setCoreTypeList(list);

			user.setPrimitiveArrayField(new long[] { 1234L });

			final SimpleDbUser.NestedEntity nestedEntity = new SimpleDbUser.NestedEntity();
			{
				nestedEntity.setNestedPrimitiveField(11);
			}

			user.setNestedEntity(nestedEntity);

			List<String> sampleJSONList = new LinkedList<String>();
			sampleJSONList.add("JSON");

			user.setObjectField(sampleJSONList);

			user.setObjectList(buildListOfObjects());

			Map<String, JSONCompatibleClass> map = new HashMap<String, JSONCompatibleClass>();
			JSONCompatibleClass json = new JSONCompatibleClass();
			json.setName("Tom");
			map.put("Tom", json);
			user.setMapOfStringAndObject(map);
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

	public static List<SimpleDbUser> createListOfItems(int length) {
		List<SimpleDbUser> list = new ArrayList<SimpleDbUser>();

		for(int i = 0; i < length; i++) {
			String itemName = "Item_" + i;
			SimpleDbUser user = createUserWithSampleAttributes(itemName);
			list.add(user);
		}
		return list;
	}
	
	public static List<SimpleDbUser> createUsersWithPrimitiveFields(float[] primitiveFields) {
		List<SimpleDbUser> users = SimpleDbUserBuilder.createListOfItems(primitiveFields.length);
		int i = 0;
		for(SimpleDbUser user : users) {
			user.setPrimitiveField(primitiveFields[i]);
			i++;
		}

        return users;
	}
}
