package org.springframework.data.simpledb.exception;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.springframework.dao.*;
import org.springframework.data.mapping.model.MappingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.model.ResourceNotFoundException;
import com.amazonaws.services.simpledb.model.*;

public class SimpleDbExceptionTranslatorTest {

	private final SimpleDbExceptionTranslator translator = SimpleDbExceptionTranslator.getTranslatorInstance();

	@Test
	public void translateExceptionIfPossible_should_translate_DuplicateItemNameException_into_DuplicateKeyException() {
		DuplicateItemNameException duplicateItemException = new DuplicateItemNameException("Duplicate Item");

		DataAccessException dataAccessException = translator.translateExceptionIfPossible(duplicateItemException);
		assertThat(dataAccessException, is(instanceOf(DuplicateKeyException.class)));

		assertThat(dataAccessException, is(notNullValue()));
		assertThat(dataAccessException.getLocalizedMessage(), StringContains.containsString("Duplicate Item"));
	}

	@Test
	public void translateExceptionIfPossible_should_translate_AttributeDoesNotExistException_into_EmptyResultDataAccessException() {
		AttributeDoesNotExistException attributeDoesNotExistException = new AttributeDoesNotExistException(
				"Attribute does not exist");

		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(attributeDoesNotExistException);
		assertThat(dataAccessException, is(instanceOf(EmptyResultDataAccessException.class)));

		assertThat(dataAccessException, is(notNullValue()));
		assertThat(dataAccessException.getLocalizedMessage(), is("Attribute does not exist (Service: null; Status Code: 0; Error Code: null; Request ID: null)"));
	}

	@Test
	public void translateExceptionIfPossible_should_translate_ResourceNotFoundException_into_DataRetrievalFailureException() {
		ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Resource Not Found");

		DataAccessException dataAccessException = translator.translateExceptionIfPossible(resourceNotFoundException);
		assertThat(dataAccessException, is(instanceOf(DataRetrievalFailureException.class)));

		assertThat(dataAccessException, is(notNullValue()));
		assertThat(dataAccessException.getLocalizedMessage(), StringContains.containsString("Resource Not Found"));
	}

	@Test
	public void translateExceptionIfPossible_should_translate_InvalidParameterValueException_into_InvalidDataAccessResourceUsageException() {
		InvalidParameterValueException invalidParameterValueException = new InvalidParameterValueException(
				"Invalid Parameter");

		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(invalidParameterValueException);
		assertThat(dataAccessException, is(instanceOf(InvalidDataAccessResourceUsageException.class)));

		assertThat(invalidParameterValueException, is(notNullValue()));
		assertThat(invalidParameterValueException.getLocalizedMessage(), is("Invalid Parameter (Service: null; Status Code: 0; Error Code: null; Request ID: null)"));

	}

	@Test
	public void translateExceptionIfPossible_should_translate_NoSuchDomainException_into_EmptyResultDataAccessException() {
		NoSuchDomainException noSuchDomainException = new NoSuchDomainException("No such domain");

		DataAccessException dataAccessException = translator.translateExceptionIfPossible(noSuchDomainException);

		assertThat(dataAccessException, is(instanceOf(EmptyResultDataAccessException.class)));

		assertThat(dataAccessException, is(notNullValue()));
		assertThat(dataAccessException.getLocalizedMessage(), is("No such domain (Service: null; Status Code: 0; Error Code: null; Request ID: null)"));

	}

	@Test
	public void translateExceptionIfPossible_should_translate_NumberDomainsExceededException_into_DataIntegrityViolationException() {
		NumberDomainsExceededException numberDomainsExceededException = new NumberDomainsExceededException(
				"Invalid domain number");

		DataAccessException dataAccessException = translator
				.translateExceptionIfPossible(numberDomainsExceededException);

		assertThat(dataAccessException, is(instanceOf(DataIntegrityViolationException.class)));

		assertThat(dataAccessException, is(notNullValue()));
		assertThat(dataAccessException.getLocalizedMessage(), StringContains.containsString("Invalid domain number"));
	}

	@Test
	public void translateExceptionIfPossible_should_translate_InvalidNextTokenException_into_InvalidDataAccessApiUsageException() {
		InvalidNextTokenException invalidNextTokenException = new InvalidNextTokenException("Invalid Token");

		DataAccessException dataAccessException = translator.translateExceptionIfPossible(invalidNextTokenException);

		assertThat(dataAccessException, is(instanceOf(InvalidDataAccessApiUsageException.class)));

		assertThat(dataAccessException, is(notNullValue()));
		assertThat(dataAccessException.getLocalizedMessage(), StringContains.containsString("Invalid Token"));
	}

	@Test
	public void translateExceptionIfPossible_should_translate_AmazonServiceException_into_DataAccessResourceFailureException() {
		AmazonServiceException amazonServiceException = new AmazonServiceException("Amazon internal exception");

		DataAccessException dataAccessException = translator.translateExceptionIfPossible(amazonServiceException);

		assertThat(dataAccessException, is(instanceOf(DataAccessResourceFailureException.class)));

		assertThat(dataAccessException, is(notNullValue()));
		assertThat(dataAccessException.getLocalizedMessage(),
				StringContains.containsString("Amazon internal exception"));
	}

	@Test
	public void translateExceptionIfPossible_should_translate_AmazonClientException_into_UncategorizedSpringDaoException() {
		AmazonClientException amazonClientException = new AmazonClientException("Amazon internal client exception");

		DataAccessException dataAccessException = translator.translateExceptionIfPossible(amazonClientException);

		assertThat(dataAccessException, is(instanceOf(UncategorizedSpringDaoException.class)));

		assertThat(dataAccessException, is(notNullValue()));
		assertThat(dataAccessException.getLocalizedMessage(),
				StringContains.containsString("Amazon internal client exception"));
	}

	@Test
	public void translateExceptionIfPossible_should_not_interpret_non_translated_exception_like_mapping_exceptions() {
		MappingException mappingException = new MappingException("Invalid Query");

		DataAccessException dataAccessException = translator.translateExceptionIfPossible(mappingException);

		assertThat(dataAccessException, is(nullValue()));
	}

}
