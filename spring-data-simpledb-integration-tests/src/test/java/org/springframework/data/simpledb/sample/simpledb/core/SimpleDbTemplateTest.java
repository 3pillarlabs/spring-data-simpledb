package org.springframework.data.simpledb.sample.simpledb.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.BasicSimpleDbUserRepository;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-configured-template-context.xml")
public class SimpleDbTemplateTest {

	@Autowired
	private SimpleDbOperations operations;
	
	@Autowired
	BasicSimpleDbUserRepository repository;
	
	@After
	public void tearDown() {
		repository.deleteAll();
	}
	
	@Test
	public void save_should_persist_single_item() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		operations.createOrUpdate(user);
		
		SimpleDbUser foundUser = operations.read(user.getItemName(), user.getClass());

		assertEquals(user.getItemName(), foundUser.getItemName());
		assertEquals(user, foundUser);
	}
	
	@Test
	public void save_should_create_new_item_for_modified_item_name() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		operations.createOrUpdate(user);

		itemName = "SecondItem";
		user.setItemName(itemName);
		operations.createOrUpdate(user);

		SimpleDbUser foundUser = operations.read("SecondItem", SimpleDbUser.class);

		assertNotNull(foundUser);
		assertEquals(user, foundUser);

		foundUser = operations.read("FirstItem", SimpleDbUser.class);
		assertNotNull(foundUser);
	}
	
	@Test
	public void delete_should_remove_item() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		user = operations.createOrUpdate(user);

		operations.delete(user);

		user = operations.read(itemName, SimpleDbUser.class);
		assertNull(user);
	}
	
	@Test
	public void consistent_count_should_return_total_number_of_item_with_no_delay() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		operations.createOrUpdate(user);
		assertEquals(1, operations.count(user.getClass()));

		operations.delete(user);
		assertEquals(0, operations.count(user.getClass()));
	}
	
	@Test
	public void save_should_generateId() {

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(null);

		user = operations.createOrUpdate(user);

		assertNotNull(user.getItemName());
	}

}
