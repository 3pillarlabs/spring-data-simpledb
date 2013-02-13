package org.springframework.data.simpledb.sample.simpledb.repository.query;

import java.util.List;
import static org.junit.Assert.*;
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
    public void selectSinglePartialField_should_return_a_single_column_in_a_single_row() {
        SimpleDbUser testUser = SimpleDbUserBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(testUser);

        List<List<Object>> result = repository.selectCoreFieldByItemName(testUser.getItemName());
        assertNotNull(result);
        assertEquals(testUser.getCoreField(), result.get(0).get(0));
    }

    @Test
    public void selectMultiplePartialField_should_return_a_multiple_columns_in_a_single_row() {
        SimpleDbUser testUser = SimpleDbUserBuilder.createUserWithSampleAttributes("Item_0");
        repository.save(testUser);

        List<List<Object>> result = repository.selectPrimitiveField_CoreFieldByItemName(testUser.getItemName());
        assertNotNull(result);
        assertEquals(testUser.getPrimitiveField(), result.get(0).get(0));
        assertEquals(testUser.getCoreField(), result.get(0).get(1));
    }

    @Test
    public void selectSinglePartialFieldList_should_return_a_single_column_with_multiple_rows() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<List<Object>> result = repository.selectCoreFields();
        assertNotNull(result);
        assertEquals(testUsers.get(2).getCoreField(), result.get(2).get(0));
    }

    @Test
    public void selectMultiplePartialFieldList_should_return_multiple_columns_with_multiple_rows() {
        List<SimpleDbUser> testUsers = SimpleDbUserBuilder.createListOfItems(3);
        repository.save(testUsers);

        List<List<Object>> result = repository.selectPrimitiveFields_CoreFields();
        assertNotNull(result);
        assertEquals(testUsers.get(2).getPrimitiveField(), result.get(2).get(0));
        assertEquals(testUsers.get(2).getCoreField(), result.get(2).get(1));
    }
}
