package org.springframework.data.simpledb.sample.simpledb.repository;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbReferences;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbReferences.FirstNestedEntity;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbReferences.SecondNestedEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-repository-context.xml")
public class SimpleDbReferencesRepositoryTest {

	@Autowired
	private SimpleDbReferencesRepository referencesRepository;
	
	@Autowired
	private SimpleDbOperations operations;

	@Before
	public void tearDown() {
		referencesRepository.deleteAll();
		
		operations.deleteAll(SecondNestedEntity.class);
		operations.deleteAll(FirstNestedEntity.class);	
	}
	
	@Test
	public void should_persist_reference_entities_in_separate_domains() {
		final SimpleDbReferences domainEntity = new SimpleDbReferences();
		
		final FirstNestedEntity nestedEntity1 = new FirstNestedEntity();
		nestedEntity1.setItemName("nested_entity_1");
		
		final SecondNestedEntity nestedEntity2 = new SecondNestedEntity();
		
		nestedEntity1.setSecondNestedEntity(nestedEntity2);
		
		domainEntity.setFirstNestedEntity(nestedEntity1);
		
		referencesRepository.save(domainEntity);
		
		final SimpleDbReferences foundReferences = referencesRepository.findOne(domainEntity.getItemName());
		final FirstNestedEntity foundFirstNestedEntity = operations.read(nestedEntity1.getItemName(), FirstNestedEntity.class);
		final SecondNestedEntity foundSecondNestedEntity = operations.read(nestedEntity2.getItemName(), SecondNestedEntity.class);
		
		/* 
		 * We haven't implemented deserialization for reference attributes, therefore we are testing for 
		 * ID equalito only. This proves that they were saved in separate domains!
		 */
		assertNotNull(foundReferences);
		assertEquals(domainEntity.getItemName(), foundReferences.getItemName());
		
		assertNotNull(foundFirstNestedEntity);
		assertEquals(nestedEntity1.getItemName(), foundFirstNestedEntity.getItemName());
		
		assertNotNull(foundSecondNestedEntity);
		assertEquals(nestedEntity2.getItemName(), foundSecondNestedEntity.getItemName());
		assertEquals(nestedEntity2.getPrimitive(), foundSecondNestedEntity.getPrimitive());
	}
}
