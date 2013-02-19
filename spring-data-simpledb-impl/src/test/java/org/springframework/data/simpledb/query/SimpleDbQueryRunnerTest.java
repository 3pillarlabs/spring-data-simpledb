package org.springframework.data.simpledb.query;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

public class SimpleDbQueryRunnerTest {
	
	@SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
	public void executeSingleResultQuery_should_fail_if_multiple_results_are_retrieved() {
		
		SimpleDbOperations<SampleEntity, String> simpleDbOperations = Mockito.mock(SimpleDbOperations.class);
		
		List<SampleEntity> sampleMultipleResults = new ArrayList<>();
		sampleMultipleResults.add(new SampleEntity());
		sampleMultipleResults.add(new SampleEntity());
		
		Mockito.when(simpleDbOperations.find(Mockito.any(SimpleDbEntityInformation.class), Mockito.anyString(), Mockito.anyBoolean()))
											.thenReturn(sampleMultipleResults);
		
		SimpleDbQueryRunner runner = new SimpleDbQueryRunner((SimpleDbOperations) simpleDbOperations, SampleEntity.class, null);
		
		runner.executeSingleResultQuery();
	}

}
