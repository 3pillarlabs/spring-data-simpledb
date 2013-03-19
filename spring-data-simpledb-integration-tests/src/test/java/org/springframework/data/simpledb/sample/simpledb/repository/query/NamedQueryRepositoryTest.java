package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class NamedQueryRepositoryTest {

	@Autowired
	private NamedQueryRepository repository;
	
    @Before
    public void setUp() {
    	List<SimpleDbUser> simpleDbUsers = SimpleDbUserBuilder.createListOfItems(3);
    	repository.save(simpleDbUsers);
    }
    
	@Test
	public void should_get_user_by_primite_field() {
		repository.findByItemNameAndPrimitiveFieldGreaterThanOrCoreFieldLike("Item_1", 1.0f, "%asd");
	}
}
