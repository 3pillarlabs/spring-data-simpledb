package org.springframework.data.simpledb.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataRetrievalFailureException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.model.ResourceNotFoundException;

public class SimpleDbOperationRetrierTest {

	@Test
	public void executeWithRetries_should_fail_for_exceeded_retries() throws Exception {

		ServiceUnavailableOperationRetrier retrier = new ServiceUnavailableOperationRetrier() {

			@Override
			public void execute() {
				AmazonServiceException serviceException = new AmazonServiceException("Test message");
				serviceException.setStatusCode(SERVICE_UNAVAILABLE_STATUS_CODE);
				serviceException.setErrorType(AmazonServiceException.ErrorType.Service);
				throw serviceException;
			}
		};

		try {
			retrier.executeWithRetries(3);
			fail("Number of retries should be exceeded");
		} catch(DataAccessResourceFailureException e) {
			// Our Exception -- ...times
			assertThat(e.getMessage(), StringContains.containsString("times"));
		}
	}

	@Test
	public void executeWithRetries_should_not_retry_if_no_exception_thrown() {
		ServiceUnavailableOperationRetrier retrier = new ServiceUnavailableOperationRetrier() {

			@Override
			public void execute() {
				// don't throw any exception
			}
		};

		retrier.executeWithRetries(3);
		Assert.assertThat(retrier.getCurrentRetry(), is(0));
	}

	@Test(expected = DataRetrievalFailureException.class)
	public void executeWithRetries_should_translate_unrecognized_exceptions() {
		ServiceUnavailableOperationRetrier retrier = new ServiceUnavailableOperationRetrier() {

			@Override
			public void execute() {
				throw new ResourceNotFoundException("Test Mapping");
			}
		};
		retrier.executeWithRetries(3);
	}
}
