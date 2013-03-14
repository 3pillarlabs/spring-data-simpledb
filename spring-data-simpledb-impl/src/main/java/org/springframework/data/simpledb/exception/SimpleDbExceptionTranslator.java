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

	private static SimpleDbExceptionTranslator instance;

	private SimpleDbExceptionTranslator() { }

	public static synchronized SimpleDbExceptionTranslator getTranslatorInstance() {
		if(instance == null) {
			instance = new SimpleDbExceptionTranslator();
		}
		
		return instance;
	}

	public static RuntimeException translateAmazonClientException(AmazonClientException e) {
		RuntimeException translatedException = getTranslatorInstance().translateExceptionIfPossible(e);
		if(translatedException == null) {
			translatedException = e;
		}
		
		return translatedException;
	}
	
	@Override
	public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        final String errorMessage = e.getLocalizedMessage();

		if(e instanceof DuplicateItemNameException) {
			return new DuplicateKeyException(errorMessage, e);
		}

		// The specified attribute does not exist.
		if(e instanceof AttributeDoesNotExistException) {
			return new EmptyResultDataAccessException(errorMessage, -1);
		}

		if(e instanceof ResourceNotFoundException) {
			return new DataRetrievalFailureException(errorMessage, e);
		}


		if(e instanceof InvalidParameterValueException) {
			return new InvalidDataAccessResourceUsageException(errorMessage, e);
		}

		if(e instanceof NoSuchDomainException) {
			return new EmptyResultDataAccessException(errorMessage, -1);
		}

		if((e instanceof NumberDomainAttributesExceededException) || (e instanceof NumberDomainsExceededException)) {
			return new DataIntegrityViolationException(errorMessage, e);
		}

		if((e instanceof InvalidNextTokenException) || (e instanceof TooManyRequestedAttributesException)
				|| (e instanceof MissingParameterException) || (e instanceof TooManyRequestedAttributesException)) {
			return new InvalidDataAccessApiUsageException(errorMessage, e);
		}

		if(e instanceof AmazonServiceException) {
			return new DataAccessResourceFailureException(errorMessage, e);
		}

		// Amazon Internal Exception
		if(e instanceof AmazonClientException) {
			return new UncategorizedSpringDaoException(errorMessage, e);
		}

		// this line means that spring-data exceptions will not be translated to DataAccessException, being interpreted
		// as they are from the SimpleDB Template
		return null;
	}
}
