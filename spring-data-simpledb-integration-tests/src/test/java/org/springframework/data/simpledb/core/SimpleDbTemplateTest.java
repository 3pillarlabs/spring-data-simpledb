package org.springframework.data.simpledb.core;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-configured-template-context.xml")
public class SimpleDbTemplateTest {

	@Autowired
	private SimpleDbOperations operations;

	@After
	public void tearDown() {
		operations.deleteAll(SimpleDbUser.class);
	}

	@Test
	public void save_should_persist_single_item() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder
				.createUserWithSampleAttributes(itemName);
		operations.createOrUpdate(user);

		SimpleDbUser foundUser = operations.read(user.getItemName(),
				user.getClass());

		assertEquals(user.getItemName(), foundUser.getItemName());
		assertEquals(user, foundUser);
	}

	@Test
	public void save_should_create_new_item_for_modified_item_name() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder
				.createUserWithSampleAttributes(itemName);
		operations.createOrUpdate(user);

		itemName = "SecondItem";
		user.setItemName(itemName);
		operations.createOrUpdate(user);

		SimpleDbUser foundUser = operations.read("SecondItem",
				SimpleDbUser.class);

		assertNotNull(foundUser);
		assertEquals(user, foundUser);

		foundUser = operations.read("FirstItem", SimpleDbUser.class);
		assertNotNull(foundUser);
	}

	@Test
	public void delete_should_remove_item() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder
				.createUserWithSampleAttributes(itemName);
		user = operations.createOrUpdate(user);

		operations.delete(user);

		user = operations.read(itemName, SimpleDbUser.class);
		assertNull(user);
	}

	@Test
	public void consistent_count_should_return_total_number_of_item_with_no_delay() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder
				.createUserWithSampleAttributes(itemName);
		operations.createOrUpdate(user);
		assertEquals(1, operations.count(user.getClass()));

		operations.delete(user);
		assertEquals(0, operations.count(user.getClass()));
	}

	@Test
	public void save_should_generateId() {

		SimpleDbUser user = SimpleDbUserBuilder
				.createUserWithSampleAttributes(null);

		user = operations.createOrUpdate(user);

		assertNotNull(user.getItemName());
	}

	@Test
	public void save_should_persist_inner_nested_entities() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder
				.createUserWithSampleAttributes(itemName);
		SimpleDbUser.NestedEntity.InnerNestedEntity innerNestedEntity = new SimpleDbUser.NestedEntity.InnerNestedEntity();
		final String innerNestedFieldValue = "innerNestedFieldValue";
		innerNestedEntity.setInnerNestedField(innerNestedFieldValue);
		user.getNestedEntity().setInnerNestedEntity(innerNestedEntity);

		operations.createOrUpdate(user);

		SimpleDbUser foundUser = operations.read(user.getItemName(),
				user.getClass());

		assertEquals(user.getItemName(), foundUser.getItemName());
		assertEquals(user, foundUser);
		assertEquals(innerNestedFieldValue, user.getNestedEntity()
				.getInnerNestedEntity().getInnerNestedField());

	}

	@Test
	public void save_should_persist_fields_with_persistent_annotation()
			throws Exception {

		final String fieldValue = "persistedFieldValue";
		SimpleDbUser user = new SimpleDbUser(fieldValue);
		operations.createOrUpdate(user);

		SimpleDbUser foundUser = operations.read(user.getItemName(),
				user.getClass());
		assertNotNull(foundUser);

		Field persistentField = foundUser.getClass().getDeclaredField(
				"persistedField");
		if (!persistentField.isAccessible()) {
			persistentField.setAccessible(true);
		}
		String foundFieldValue = (String) persistentField.get(foundUser);
		assertEquals(fieldValue, foundFieldValue);

	}

	@Test
	public void save_should_not_persist_transient_fields() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder
				.createUserWithSampleAttributes(itemName);
		user.setTransientField("transientFieldValue");
		operations.createOrUpdate(user);

		SimpleDbUser foundUser = operations.read(user.getItemName(),
				user.getClass());
		assertNull(foundUser.getTransientField());
	}

	@Test
	public void update_entity_should_update_all_given_fields() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder
				.createUserWithSampleAttributes(itemName);
		operations.createOrUpdate(user);

		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("primitiveField", 0.02f);
		propertyMap.put("coreField", "test_string$");
		propertyMap.put("primitiveArrayField", new long[] { 1235L, 1236L });
		propertyMap.put("coreTypeList", Arrays.asList(Integer.valueOf(123),
				Integer.valueOf(23), Integer.valueOf(3)));
		propertyMap.put("nestedEntity.nestedPrimitiveField", 12);
		SimpleDbUser.NestedEntity.InnerNestedEntity sni = new SimpleDbUser.NestedEntity.InnerNestedEntity();
		sni.setInnerNestedField("innerNestedFieldValue");
		propertyMap.put("nestedEntity.innerNestedEntity", sni);
		operations.update(itemName, SimpleDbUser.class, propertyMap);

		SimpleDbUser found = operations.read(user.getItemName(),
				SimpleDbUser.class);
		assertEquals(propertyMap.get("primitiveField"),
				found.getPrimitiveField());
		assertEquals(propertyMap.get("coreField"), found.getCoreField());
		assertArrayEquals((long[]) propertyMap.get("primitiveArrayField"),
				found.getPrimitiveArrayField());
		assertEquals(propertyMap.get("coreTypeList"), found.getCoreTypeList());
		assertEquals(propertyMap.get("nestedEntity.nestedPrimitiveField"),
				found.getNestedEntity().getNestedPrimitiveField());
		assertEquals(propertyMap.get("nestedEntity.innerNestedEntity"), found
				.getNestedEntity().getInnerNestedEntity());

	}

}
