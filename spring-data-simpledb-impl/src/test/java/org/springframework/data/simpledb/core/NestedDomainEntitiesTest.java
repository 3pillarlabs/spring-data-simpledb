package org.springframework.data.simpledb.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.core.NestedDomainEntitiesTest.AClass.BClass;
import org.springframework.data.simpledb.reflection.MetadataParser;

public class NestedDomainEntitiesTest {

	@SuppressWarnings("unused")
	static class AClass {

		@Id
		private String id;

		private int intField;
		private BClass nestedB;

		private Integer integerField;
		private List<Float> floatList;

		static class BClass {

			private int intField;
		}
	}

	@Test
	public void should_parse_only_valid_nested_domain_entities() {
		final List<Field> parsedNestedEntities = MetadataParser.getNestedDomainFields(new AClass());

		assertTrue(parsedNestedEntities.size() == 1);
		assertEquals(BClass.class, parsedNestedEntities.get(0).getType());
	}

}
