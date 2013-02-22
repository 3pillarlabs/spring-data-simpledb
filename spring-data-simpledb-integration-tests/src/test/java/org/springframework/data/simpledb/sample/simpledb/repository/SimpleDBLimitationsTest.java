package org.springframework.data.simpledb.sample.simpledb.repository;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.JSONCompatibleClass;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.simpledb.model.InvalidParameterValueException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class SimpleDBLimitationsTest {

    public static final int MAX_SIMPLE_DB_ATTRIBUTE_LENGTH = 1024;
    @Autowired
	BasicSimpleDbUserRepository repository;

	@After
	public void tearDown() {
		repository.deleteAll();
	}



	@Test
	public void should_persist_max_simple_db_length_characters_long_string() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);

		user.setCoreField(generateStringOfLength(MAX_SIMPLE_DB_ATTRIBUTE_LENGTH));

		repository.save(user);

		SimpleDbUser foundUser = repository.findOne(user.getItemName());

		assertNotNull(foundUser);
		assertEquals(MAX_SIMPLE_DB_ATTRIBUTE_LENGTH, foundUser.getCoreField().length());
		assertEquals(user.getCoreField(), foundUser.getCoreField());
	}

    @Test
	public void should_persist_strings_longer_than_max_simple_db_length_chars() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);

        user.setCoreField(generateStringOfLength(MAX_SIMPLE_DB_ATTRIBUTE_LENGTH+1));

		repository.save(user);

        SimpleDbUser foundUser = repository.findOne(user.getItemName());

        assertNotNull(foundUser);
        assertEquals(MAX_SIMPLE_DB_ATTRIBUTE_LENGTH+1, foundUser.getCoreField().length());
        assertEquals(user.getCoreField(), foundUser.getCoreField());
	}

	/* max 256 attributes in a amazon simpleDB request */
	@Test
	public void should_persist_array_serialized_into_string_longer_than_max_simple_db_length_chars() {
		String itemName = "FirstItem";

		SimpleDbUser user = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);

		final long[] longPrimitiveArray = new long[512];
		for(int i = 0; i < 10; i++) {
			longPrimitiveArray[i] = i;
		}
		user.setPrimitiveArrayField(longPrimitiveArray);

		repository.save(user);
		
		final SimpleDbUser foundUser = repository.findOne(user.getItemName());
		assertNotNull(foundUser);
        assertEquals(512, foundUser.getPrimitiveArrayField().length);
        assertTrue(Arrays.equals(user.getPrimitiveArrayField(), foundUser.getPrimitiveArrayField()));
	}

    private static String generateStringOfLength(int length){
        final StringBuilder sb = new StringBuilder();
        while(sb.length() < length) {
            sb.append("a");
        }
        return sb.toString();
    }



}
