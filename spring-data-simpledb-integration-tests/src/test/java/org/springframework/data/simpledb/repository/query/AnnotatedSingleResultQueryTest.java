package org.springframework.data.simpledb.repository.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class AnnotatedSingleResultQueryTest {

	@Autowired
	AnnotatedSingleResultQueryRepository repository;

    static List<SimpleDbUser> simpleDbUsers;

    //Used for performance reasons to delete after class all simpleDbUsers created
    static AnnotatedSingleResultQueryRepository staticRepository;

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

	@Test
	public void customLongCount_should_return_the_number_of_users_represented_as_Long() {
        //created in setup a list of 3 SimpleDbUser - first Item_0
		long result = repository.customLongCount();
		assertNotNull(result);
		assertEquals(simpleDbUsers.size(), result);
	}

	@Test
	public void customSelectOneUser_should_return_one_user() {
        //created in setup a list of 3 SimpleDbUser - first Item_0

		SimpleDbUser result = repository.customSelectOneUser();
		assertNotNull(result);
		assertEquals(simpleDbUsers.get(0), result);
	}

	@Test
	public void partialPrimitiveFieldSelect_should_return_a_single_primitive_field() {
        //created in setup a list of 3 SimpleDbUser - first Item_0

		float result = repository.partialPrimitiveFieldSelect();
		assertNotNull(result);
		assertThat(result, is( simpleDbUsers.get(0).getPrimitiveField()));
	}
}
