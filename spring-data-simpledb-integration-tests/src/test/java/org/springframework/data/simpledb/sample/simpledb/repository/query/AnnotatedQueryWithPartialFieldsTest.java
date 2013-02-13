package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class AnnotatedQueryWithPartialFieldsTest {

    @Autowired
    AnnotatedQueryWithPartialFieldsReqpository repository;

    @Test
    @Ignore
    public void selectSinglePartialField_should_return_a_single_column_in_a_single_row() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<List<Object>> result = repository.selectSinglePartialField("Item_0");
        assertNotNull(result);
        //TODO
    }

    @Test
    @Ignore
    public void selectMultiplePartialField_should_return_a_multiple_columns_in_a_single_row() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<List<Object>> result = repository.selectMultiplePartialField("Item_0");
        assertNotNull(result);
        //TODO
    }

    @Test
    @Ignore
    public void selectSinglePartialFieldList_should_return_a_single_column_with_multiple_rows() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<List<Object>> result = repository.selectSinglePartialFieldList();
        assertNotNull(result);
        //TODO
    }

    @Test
    public void selectMultiplePartialFieldList_should_return_multiple_columns_with_multiple_rows() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<List<Object>> result = repository.selectMultiplePartialFieldList();
        assertNotNull(result);
        //TODO
    }
}
