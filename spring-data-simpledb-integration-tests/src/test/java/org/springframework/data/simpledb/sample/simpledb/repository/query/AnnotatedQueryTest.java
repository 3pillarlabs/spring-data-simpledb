package org.springframework.data.simpledb.sample.simpledb.repository.query;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Set;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.exception.InvalidSimpleDBQueryException;
import org.springframework.data.simpledb.sample.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class AnnotatedQueryTest {


	@Autowired
	AnnotatedQueryRepository repository;

    static List<SimpleDbUser> simpleDbUsers;

    //Used for performance reasons to delete after class all simpleDbUsers created
    static AnnotatedQueryRepository staticRepository;

    @Before
    public void setUp() {
        //for performance reasons create 3 entities once and use them to test all queries
        if(simpleDbUsers == null){
            simpleDbUsers = SimpleDbUserBuilder.createListOfItems(3);
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

	@Test(expected = IllegalArgumentException.class)
	public void customSelectAllWrongReturnType_should_fail_wrong_returned_collection_generic_type() {
		repository.customSelectAllWrongReturnType();
	}

	@Test
	public void customSelectWithNamedParamsQuery_should_return_a_list_of_objects() {
        //created in setup a list of 3 SimpleDbUser - first Item_0

		List<SimpleDbUser> result = repository.customSelectWithNamedParamsQuery(String.valueOf(0.01f), "Item_1");
		assertNotNull(result);
	}

	@Test
	public void customSelectWithIndexedParams_should_return_a_list_of() {
        //created in setup a list of 3 SimpleDbUser - first Item_0

		List<SimpleDbUser> result = repository.customSelectWithIndexedParams(String.valueOf("tes_string$"), String.valueOf(0.01f));
		assertNotNull(result);
	}

	@Test
	public void partialPrimitiveSetSelect_should_return_a_set_of_primitives() {
        //created in setup a list of 3 SimpleDbUser - first Item_0

		Set<Float> result = repository.primitiveFieldSelect();
		assertNotNull(result);
		for(SimpleDbUser entity : simpleDbUsers) {
			assertTrue(result.contains(entity.getPrimitiveField()));
		}
	}

	@Test
	public void partialObjectListSelect_should_return_a_single_object_field() {
        //created in setup a list of 3 SimpleDbUser - first Item_0

		List<JSONCompatibleClass> result = repository.partialObjectListSelect();
		assertNotNull(result);
	}

	@Test
	public void partialListOfListField_should_return_a_list_of_core_object_fields() {
        //created in setup a list of 3 SimpleDbUser - first Item_0

		List<List<Object>> result = repository.selectCoreFields();
		assertEquals(1, result.size()); // one row

		// one column
		List<Object> columns = result.get(0);
		assertEquals(1, columns.size()); // one row
	}

	@Test
	public void custom_select_with_where_clause_should_work() {
        //created in setup a list of 3 SimpleDbUser - first Item_0

		List<SimpleDbUser> result = repository.customSelectWithWhereClause();
		assertEquals(1, result.size()); // one row

		// one column
		SimpleDbUser simpelDbUser = result.get(0);
		assertEquals("Item_0", simpelDbUser.getItemName());
	}

	@Test(expected = InvalidSimpleDBQueryException.class)
	public void malformedQuery_should_throw_exception() {
		repository.malformedQuery();
	}
}
