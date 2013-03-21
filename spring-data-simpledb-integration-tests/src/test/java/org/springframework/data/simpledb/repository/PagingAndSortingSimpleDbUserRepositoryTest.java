package org.springframework.data.simpledb.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class PagingAndSortingSimpleDbUserRepositoryTest {

	@Autowired
	PagingAndSortingUserRepository repository;

    static List<SimpleDbUser> simpleDbUsers;

    //Used for performance reasons to delete after class all simpleDbUsers created
    static PagingAndSortingUserRepository staticRepository;

    @Before
    public void setUp() {
        //for performance reasons create 3 entities once and use them to test all queries
        if(simpleDbUsers == null){
            simpleDbUsers = SimpleDbUserBuilder.createListOfItems(5);
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
	public void findAll_with_sort_should_return_desc_items() {
        //created in setup a list of 5 SimpleDbUser - first Item_0

		Iterable<SimpleDbUser> findAll = repository.findAll(new Sort(new Order(Sort.Direction.DESC, "itemName")));

		assertEquals(simpleDbUsers.get(simpleDbUsers.size() - 1).getItemName(), findAll.iterator().next().getItemName());
	}

	@Test
	public void findAll_with_pageable_should_return_paged_result() {
        //created in setup a list of 5 SimpleDbUser - first Item_0

		final int pageNumber = 2;
		final int pageSize = 2;
		PageRequest pageable = new PageRequest(pageNumber, pageSize);
		final Page<SimpleDbUser> page = repository.findAll(pageable);

		assertNotNull(page);
		assertEquals(pageNumber, page.getNumber());

		List<SimpleDbUser> content = page.getContent();
		assertEquals(pageSize, content.size());

		/* users 3 & 4 */
		assertEquals(simpleDbUsers.get(2), content.get(0));
		assertEquals(simpleDbUsers.get(3), content.get(1));
	}

	@Test
	public void findAll_should_return_page_with_last_element_only() {
        //created in setup a list of 5 SimpleDbUser - first Item_0

		final int pageNumber = 3;
		final int pageSize = 2;
		PageRequest pageable = new PageRequest(pageNumber, pageSize);
		final Page<SimpleDbUser> page = repository.findAll(pageable);

		assertNotNull(page);
		assertEquals(pageNumber, page.getNumber());

		List<SimpleDbUser> content = page.getContent();
		assertEquals(1, content.size());

		/* users 5 */
		assertEquals(simpleDbUsers.get(4), content.get(0));
	}

	@Test
	public void findAll_should_return_empty_list_for_non_existing_page() {
        //created in setup a list of 5 SimpleDbUser - first Item_0

		final int pageNumber = 4;
		final int pageSize = 2;
		PageRequest pageable = new PageRequest(pageNumber, pageSize);
		final Page<SimpleDbUser> page = repository.findAll(pageable);

		assertNotNull(page);
		assertEquals(5, page.getTotalElements());

		List<SimpleDbUser> content = page.getContent();
		assertNotNull(content);
		assertEquals(0, content.size());
	}

}
