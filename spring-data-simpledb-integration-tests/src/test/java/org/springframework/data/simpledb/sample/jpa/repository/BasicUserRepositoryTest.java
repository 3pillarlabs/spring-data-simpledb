package org.springframework.data.simpledb.sample.jpa.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.jpa.domain.User;
import org.springframework.data.simpledb.sample.jpa.repository.BasicUserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

/**
 * Integration test showing the basic usage of {@link org.springframework.data.simpledb.sample.jpa.repository.BasicUserRepository}.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-repository-context.xml")
@Transactional
public class BasicUserRepositoryTest {

	@Autowired
    BasicUserRepository repository;
	User user;

	@Before
	public void setUp() {

		user = new User();
		user.setUsername("foobar");
		user.setFirstName("firstname");
		user.setLastName("lastname");
	}

	/**
	 * Tests inserting a user and asserts it can be loaded again.
	 */
	@Test
	public void findOne_should_return_saved_user() {

		user = repository.save(user);

		assertEquals(user, repository.findOne(user.getId()));
	}


	@Test
	public void findAll_should_return_saved_user() throws Exception {

		user = repository.save(user);

		Iterable<User> users = repository.findAll();

		assertEquals("firstname", users.iterator().next().getFirstName());
	}
}
