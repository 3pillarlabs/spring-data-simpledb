package org.springframework.data.simpledb.repository;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.repository.BasicSimpleDbUserRepository;
import org.springframework.data.simpledb.repository.util.IncrementalWait;
import org.springframework.data.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-repository-context.xml")
public class BasicSimpleDbUserRepositoryTest {

	@Autowired
	BasicSimpleDbUserRepository repository;

	@After
	public void tearDown() {
		repository.deleteAll();
	}

	@Test
	public void save_without_consistent_read_does_not_ensure_item_found() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);
		repository.save(user);

		incrementalWaitFindOne(user.getItemName());
		SimpleDbUser foundUser = repository.findOne(user.getItemName());

		assertEquals(user.getItemName(), foundUser.getItemName());
		assertEquals(user, foundUser);
	}

	private void incrementalWaitFindOne(final String itemName) {
		new IncrementalWait<SimpleDbUser>() {

			@Override
			public SimpleDbUser execute() {
				return repository.findOne(itemName);
			}
		}.untilResponseNotNull();
	}

}
