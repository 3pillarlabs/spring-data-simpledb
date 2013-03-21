package org.springframework.data.simpledb.repository.query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.repository.query.NamedQueryRepository;
import org.springframework.data.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class NamedQueryRepositoryTest {

	@Autowired
	private NamedQueryRepository repository;
	private static List<SimpleDbUser> simpleDbUsers;
	
    //Used for performance reasons to delete after class all simpleDbUsers created
    static NamedQueryRepository staticRepository;
	
    @Before
    public void setUp() {
        //for performance reasons create 3 entities once and use them to test all queries
        if(simpleDbUsers == null){
            simpleDbUsers = SimpleDbUserBuilder.createUsersWithPrimitiveFields(new float[]{0f, 1.0f, 2.0f, 3.0f, 4.0f, 5f});
            repository.save(simpleDbUsers);
        }
    }

    @After
    public void setUpStaticRepository(){
        staticRepository = repository;
    }

    @AfterClass
    public static void tearDown() {
        staticRepository.deleteAll();
    }
    
	@Test
	public void should_get_user_by_itemName() {
		final SimpleDbUser user = repository.findByItemName("Item_1");
		
		assertNotNull(user);
		assertEquals(simpleDbUsers.get(1), user);
	}
	
	@Test
	public void should_get_users_by_primitiveField() {
		// created users with primitive fields 0f, 1.0f, 2.0f, 3.0f, 4.0f, 5f
		final List<SimpleDbUser> users = repository.findByPrimitiveFieldGreaterThan(3.0f);
		
		assertNotNull(users);
		assertEquals(2, users.size());
		
		assertEquals(simpleDbUsers.get(4), users.get(0));
		assertEquals(simpleDbUsers.get(5), users.get(1));
	}
	
	@Test
	public void should_get_users_by_primitiveField_or_itemName() {
		// created users with primitive fields 0f, 1.0f, 2.0f, 3.0f, 4.0f, 5f
		final List<SimpleDbUser> users = repository.getByPrimitiveFieldOrItemNameLike(3.0f, "%Item_2%");
		
		assertNotNull(users);
		assertEquals(2, users.size());
		
		assertTrue(users.contains(simpleDbUsers.get(2))); // matched by LIKE "%Item_2%"
		assertTrue(users.contains(simpleDbUsers.get(3)));
	}
	
	@Test
	public void should_get_users_by_complex_query() {
		// created users with primitive fields 0f, 1.0f, 2.0f, 3.0f, 4.0f, 5f
		final List<SimpleDbUser> users = repository.findByItemNameLikeOrPrimitiveFieldGreaterThanAndPrimitiveFieldLessThan("%Item_2%", 2.0f, 4.0f);
		
		assertNotNull(users);
		assertEquals(2, users.size());
		
		assertTrue(users.contains(simpleDbUsers.get(2))); // matched by LIKE "%Item_2%"
		assertTrue(users.contains(simpleDbUsers.get(3))); // the only one between 2 and 4
	}
	
	@Test
	public void should_get_users_by_primitiveField_greaterThan_Paged() {
		final int pageNumber = 1;
		final int pageSize = 1;
		
		// created users with primitive fields 0f, 1.0f, 2.0f, 3.0f, 4.0f, 5f
		final Page<SimpleDbUser> page = repository.readByPrimitiveFieldGreaterThan(1.0f, new PageRequest(
				pageNumber, pageSize));
		
		assertNotNull(page);

		List<SimpleDbUser> content = page.getContent();
		assertEquals(1, content.size());
		assertEquals(4, page.getTotalElements());
	}
}
