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
import org.springframework.data.simpledb.parser.TokenMgrError;
import org.springframework.data.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class PagedAnnotatedQueryRepositoryTest {

	@Autowired
    PagedAnnotatedQueryRepository repository;

    static List<SimpleDbUser> simpleDbUsers;

    //Used for performance reasons to delete after class all simpleDbUsers created
    static PagedAnnotatedQueryRepository staticRepository;

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
	public void custom_queries_should_return_Page_results_for_PageRequests() {
        // created users with primitive fields 0f, 1.0f, 2.0f, 3.0f, 4.0f, 5f

		final int pageNumber = 1;
		final int pageSize = 1;
		final Page<SimpleDbUser> page = repository.findUsersWithPrimitiveFieldHigherThan(1.0f, new PageRequest(
                pageNumber, pageSize));

		assertNotNull(page);

		List<SimpleDbUser> content = page.getContent();
		assertEquals(1, content.size());
		assertEquals(4, page.getTotalElements());
	}

	@Test
	public void custom_queries_should_return_List_results_for_PageRequests() {
        // created users with primitive fields 0f, 1.0f, 2.0f, 3.0f, 4.0f, 5f

		final int pageNumber = 2;
		final int pageSize = 2;
		final List<SimpleDbUser> results = repository.findUserListWithPrimitiveFieldHigherThan(1f, new PageRequest(
                pageNumber, pageSize));

		// 4 and 5
		assertEquals(2, results.size());
		SimpleDbUser firstResult = results.get(0);
		assertThat(firstResult.getPrimitiveField(), is(4f));
	}

	@Test
	public void paged_request_should_work_for_no_elements() {
        // created users with primitive fields 0f, 1.0f, 2.0f, 3.0f, 4.0f, 5f

        final int pageNumber = 1;
		final int pageSize = 1;
		final Page<SimpleDbUser> page = repository.findUsersWithPrimitiveFieldHigherThan(6.0f, new PageRequest(
				pageNumber, pageSize));

		assertNotNull(page);
		assertEquals(0, page.getTotalElements());
		assertEquals(0, page.getTotalPages());
		assertEquals(1, page.getNumber());
		assertEquals(0, page.getNumberOfElements());
		assertNotNull(page.getContent());
	}

	@Test(expected = TokenMgrError.class)
	public void paged_request_for_invalid_query_should_throw_exception() {
		final int pageNumber = 1;
		final int pageSize = 1;
		repository.invalidQuery(new PageRequest(pageNumber, pageSize));
	}

	@Test
	public void paged_request_for_partial_field_should_return_list_of_entities_only_with_requested_fields() {
        // created users with primitive fields 0f, 1.0f, 2.0f, 3.0f, 4.0f, 5f


        final int pageNumber = 2;
		final int pageSize = 2;
		final List<SimpleDbUser> results = repository.pagedPartialQuery(1.0f, new PageRequest(pageNumber, pageSize));

		assertEquals(2, results.size());
		SimpleDbUser firstResult = results.get(0);
		assertThat(firstResult.getPrimitiveField(), is(4f));

		assertNotNull(firstResult.getItemName());

		/* the rest of the fields should be empty */
		assertNull(firstResult.getCoreField());
		assertNull(firstResult.getCoreTypeList());
		assertNull(firstResult.getObjectField());
	}

}
