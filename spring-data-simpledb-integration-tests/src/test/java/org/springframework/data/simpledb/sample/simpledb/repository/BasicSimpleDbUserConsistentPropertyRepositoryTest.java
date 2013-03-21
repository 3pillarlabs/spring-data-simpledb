package org.springframework.data.simpledb.sample.simpledb.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class BasicSimpleDbUserConsistentPropertyRepositoryTest {

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
		repository.save(user);

		SimpleDbUser foundUser = repository.findOne(user.getItemName());

		assertEquals(user.getItemName(), foundUser.getItemName());
		assertEquals(user, foundUser);
	}

	@Test
	public void save_should_persist_item_list() {
		List<SimpleDbUser> list = SimpleDbUserBuilder.createListOfItems(3);

		repository.save(list);

		assertEquals(list.size(), repository.count());
	}

	@Test
	public void save_should_create_new_item_for_modified_item_name() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		repository.save(user);

		itemName = "SecondItem";
		user.setItemName(itemName);
		repository.save(user);

		SimpleDbUser foundUser = repository.findOne("SecondItem");

		assertNotNull(foundUser);
		assertEquals(user, foundUser);

		foundUser = repository.findOne("FirstItem");
		assertNotNull(foundUser);
	}

	@Test
	public void delete_should_remove_item() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		user = repository.save(user);

		repository.delete(user);

		user = repository.findOne(itemName);
		assertNull(user);
	}

	@Test
	public void delete_should_remove_item_by_itemName() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		repository.save(user);
		repository.delete(itemName);
		user = repository.findOne(itemName);
		assertNull(user);
	}

	@Test
	public void delete_should_remove_list_of_items() {
		List<SimpleDbUser> list = SimpleDbUserBuilder.createListOfItems(3);
		repository.save(list);

		repository.delete(list);

		assertEquals(0, repository.count());
	}

	@Test
	public void findOne_should_return_one_item() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		repository.save(user);

		SimpleDbUser foundUser = repository.findOne(itemName);

		assertNotNull(foundUser);
		assertEquals(user.getItemName(), foundUser.getItemName());
		assertEquals(user, foundUser);
	}

	@Test
	public void findAll_should_return_all_items() {
		List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
		repository.save(testUsers);

		Iterable<SimpleDbUser> users = repository.findAll();

		assertNotNull(users);
		assertEquals(testUsers.size(), count(users));
	}

	@Test
	public void findAll_with_iterable_should_return_a_list_of_items() {
		List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
		repository.save(testUsers);

		SimpleDbUser first = testUsers.get(0);
		Iterable<String> ids = Arrays.asList(new String[] { first.getItemName() });
		Iterable<SimpleDbUser> foundUsers = repository.findAll(ids);

		assertNotNull(foundUsers);
		assertEquals(first.getItemName(), foundUsers.iterator().next().getItemName());
	}

	@Test
	public void exists_should_return_true_for_existing_items() {
		String itemName = "FirstItem";
		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		repository.save(user);

		assertTrue(repository.exists(user.getItemName()));
	}

	@Test
	public void consistent_find_should_return_updated_item_with_no_delay() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		repository.save(user);
		assertNotNull(repository.findOne(itemName));

		repository.delete(itemName);
		assertNull(repository.findOne(itemName));
	}

	@Test
	public void consistent_count_should_return_total_number_of_item_with_no_delay() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		repository.save(user);
		assertEquals(1, repository.count());

		repository.delete(itemName);
		assertEquals(0, repository.count());
	}

	@Test
	public void save_should_generateId() {

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(null);

		user = repository.save(user);

		assertNotNull(user.getItemName());
	}

	private int count(Iterable<SimpleDbUser> users) {
		Iterator<SimpleDbUser> iterator = users.iterator();
		int count = 0;
		while(iterator.hasNext()) {
			count++;
			iterator.next();
		}
		return count;
	}

}
