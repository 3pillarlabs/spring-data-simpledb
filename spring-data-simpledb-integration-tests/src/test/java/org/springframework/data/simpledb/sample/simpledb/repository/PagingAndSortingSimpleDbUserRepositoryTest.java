package org.springframework.data.simpledb.sample.simpledb.repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-simpledb-repository-context.xml")
//@Ignore(value = "work in progress")
public class PagingAndSortingSimpleDbUserRepositoryTest {

    @Autowired
    PagingAndSortingUserRepository repository;

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void findAll_with_sort_should_return_desc_items() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PagingAndSortingSimpleDbUserRepositoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Iterable<SimpleDbUser> findAll = repository.findAll(new Sort(new Order(Sort.Direction.DESC,"itemName()")));

        assertEquals(testUsers.get(testUsers.size()-1).getItemName(),findAll.iterator().next().getItemName());
    }


}
