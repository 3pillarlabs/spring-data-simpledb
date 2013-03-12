package org.springframework.data.simpledb.exception;

import org.springframework.dao.*;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.model.ResourceNotFoundException;
import com.amazonaws.services.simpledb.model.*;

/**
 * Simple {@link PersistenceExceptionTranslator} for SimpleDB. Translated the runtime exception to an appropriate
 * {@code org.springframework.dao} exception
 * 
 */
public final class SimpleDbExceptionTranslator implements PersistenceExceptionTranslator {

	private static final SimpleDbExceptionTranslator INSTANCE = new SimpleDbExceptionTranslator();

	private SimpleDbExceptionTranslator() {
	}

	public static SimpleDbExceptionTranslator getTranslatorInstance() {
		return INSTANCE;
	}

	@Override
	public DataAccessException translateExceptionIfPossible(RuntimeException e) {
		if(e instanceof DuplicateItemNameException) {
			return new DuplicateKeyException(e.getLocalizedMessage(), e);
		}

		// The specified attribute does not exist.
		if(e instanceof AttributeDoesNotExistException) {
			return new EmptyResultDataAccessException(e.getLocalizedMessage(), -1);
		}

		if(e instanceof ResourceNotFoundException) {
			return new DataRetrievalFailureException(e.getLocalizedMessage(), e);
		}

		if(e instanceof TooManyRequestedAttributesException) {
			return new IncorrectResultSizeDataAccessException(e.getLocalizedMessage(), -1);
		}

		if(e instanceof AmazonServiceException) {
			return new DataAccessResourceFailureException(e.getLocalizedMessage(), e);
		}

		if(e instanceof InvalidParameterValueException) {
			return new InvalidDataAccessResourceUsageException(e.getLocalizedMessage(), e);
		}

		if(e instanceof MissingParameterException) {
			return new InvalidDataAccessResourceUsageException(e.getLocalizedMessage(), e);
		}

		if(e instanceof NoSuchDomainException) {
			return new EmptyResultDataAccessException(e.getLocalizedMessage(), -1);
		}

		if(e instanceof NumberDomainAttributesExceededException || e instanceof NumberDomainsExceededException
				|| e instanceof TooManyRequestedAttributesException) {
			return new DataIntegrityViolationException(e.getLocalizedMessage(), e);
		}

		if(e instanceof InvalidNextTokenException || e instanceof TooManyRequestedAttributesException
				|| e instanceof MissingParameterException) {
			return new InvalidDataAccessApiUsageException(e.getLocalizedMessage(), e);
		}

		if(e instanceof AmazonServiceException) {

		}

		// Amazon Internal Exception
		if(e instanceof AmazonClientException) {
			return new UncategorizedSpringDaoException(e.getLocalizedMessage(), e);
		}

		// this line means that spring-data exceptions will not be translated to DataAccessException, being interpreted
		// as they are from the SimpleDB Template
		return null;
	}
}
