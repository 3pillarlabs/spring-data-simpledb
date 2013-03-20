package org.springframework.data.simpledb.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.simpledb.core.domain.SimpleDbSampleEntity;

public class PartTreeConverterTest {

	@Test
	public void should_create_corect_query_for_simple_property() {
		final String methodName = "findByItemName";
		final PartTree tree = new PartTree(methodName, SimpleDbSampleEntity.class);
		
		final String query = PartTreeConverter.toIndexedQuery(tree);
		
		final String expected = " itemName = ? ";
		
		assertEquals(expected, query);
	}
	
	@Test
	public void should_create_corect_query_for_between() {
		final String methodName = "readByAgeBetween";
		final PartTree tree = new PartTree(methodName, SimpleDbSampleEntity.class);
		
		final String query = PartTreeConverter.toIndexedQuery(tree);
		
		final String expected = " age BETWEEN ? and ? ";
		
		assertEquals(expected, query);
	}
	
	@Test
	public void should_create_corect_query_for_complex_operators() {
		final String methodName = "getByItemNameLikeOrAgeGreaterThanAndAgeLessThan";
		final PartTree tree = new PartTree(methodName, SimpleDbSampleEntity.class);
		
		final String query = PartTreeConverter.toIndexedQuery(tree);
		
		final String expected = " itemName LIKE ? OR age > ? AND age < ? ";
		
		assertEquals(expected, query);
	}
	
	@Test(expected = MappingException.class) 
	public void shoud_fail_for_unsupported_operator() {
		final String methodName = "readByAgeEndsWith";
		final PartTree tree = new PartTree(methodName, SimpleDbSampleEntity.class);
		
		PartTreeConverter.toIndexedQuery(tree);
	}
	
}
