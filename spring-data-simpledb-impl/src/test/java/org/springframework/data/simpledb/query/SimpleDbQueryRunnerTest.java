package org.springframework.data.simpledb.query;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.simpledb.core.SimpleDbOperations;

public class SimpleDbQueryRunnerTest {

	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void executeSingleResultQuery_should_fail_if_multiple_results_are_retrieved() {

		SimpleDbOperations simpleDbOperations = Mockito.mock(SimpleDbOperations.class);

		List<SampleEntity> sampleMultipleResults = new ArrayList<SampleEntity>();
		sampleMultipleResults.add(new SampleEntity());
		sampleMultipleResults.add(new SampleEntity());

		Mockito.when(simpleDbOperations.find(Mockito.same(SampleEntity.class), Mockito.anyString())).thenReturn(
				sampleMultipleResults);

		SimpleDbQueryRunner runner = new SimpleDbQueryRunner((SimpleDbOperations) simpleDbOperations,
				SampleEntity.class, null);

		runner.executeSingleResultQuery();
	}

	@Test
	public void getSingleResult_should_return_null_for_empty_list() {
		SimpleDbQueryRunner runner = new SimpleDbQueryRunner(null, SampleEntity.class, null);

		final Object result = runner.getSingleResult(new ArrayList<SampleEntity>());

		assertNull(result);
	}

	@Test
	public void getSingleResult_should_return_single_value_from_list_with_one_element() {
		SimpleDbQueryRunner runner = new SimpleDbQueryRunner(null, SampleEntity.class, null);

		final ArrayList<SampleEntity> results = new ArrayList<SampleEntity>();
		results.add(new SampleEntity());

		final Object result = runner.getSingleResult(results);

		assertNotNull(result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getSingleResult_should_fail_for_list_with_multiple_elements() {
		SimpleDbQueryRunner runner = new SimpleDbQueryRunner(null, SampleEntity.class, null);

		final ArrayList<SampleEntity> results = new ArrayList<SampleEntity>();
		results.add(new SampleEntity());
		results.add(new SampleEntity());

		runner.getSingleResult(results);
	}

}
