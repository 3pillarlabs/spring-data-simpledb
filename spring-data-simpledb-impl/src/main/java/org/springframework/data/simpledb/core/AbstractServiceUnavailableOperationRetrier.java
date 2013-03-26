package org.springframework.data.simpledb.core;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.simpledb.exception.SimpleDbExceptionTranslator;

/**
 * Enables callbacks on execute()
 */
public abstract class AbstractServiceUnavailableOperationRetrier {

    private int serviceUnavailableRetries;

    public AbstractServiceUnavailableOperationRetrier(int serviceUnavailableRetries) {
        this.serviceUnavailableRetries = serviceUnavailableRetries;
    }

    private static final int RETRY_TIME = 400;
    private int currentRetry = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimpleDbTemplate.class);
    public static final int SERVICE_UNAVAILABLE_STATUS_CODE = 503;

    /**
     * Override this method for specific operations that need retry for Amazon ServiceUnavailableException
     */
    public abstract void execute();

    public final void executeWithRetries() {
        try {
            AmazonClientException serviceUnavailableException = null;
            serviceUnavailableException = tryExecute();

            while ((serviceUnavailableException != null) && currentRetry < serviceUnavailableRetries) {
                try {
                    Thread.sleep(RETRY_TIME);
                } catch (InterruptedException e) {
                    LOGGER.debug(e.getLocalizedMessage());
                }

                LOGGER.debug("Retrying operation");
                currentRetry++;
                serviceUnavailableException = tryExecute();
            }

            if (currentRetry == serviceUnavailableRetries) {
                throw new DataAccessResourceFailureException(
                        "SimpleDB operation failed for " + currentRetry + " times", serviceUnavailableException);
            }
        } catch (AmazonClientException exception) {
            throw SimpleDbExceptionTranslator.getTranslatorInstance().translateAmazonClientException(exception);
        }

    }

    /**
     * @return recognized exception or null, throws further not recognized exception
     */
    private AmazonClientException tryExecute() {
        try {
            execute();
        } catch (AmazonClientException clientException) {
            if (isServiceUnavailableException(clientException)) {
                return clientException;
            }

            throw clientException;
        }

        return null;
    }

    private boolean isServiceUnavailableException(AmazonClientException e) {
        return (((AmazonServiceException) e).getErrorType() == AmazonServiceException.ErrorType.Service
                && ((AmazonServiceException) e).getStatusCode() == AbstractServiceUnavailableOperationRetrier.SERVICE_UNAVAILABLE_STATUS_CODE);

    }

    public int getCurrentRetry() {
        return currentRetry;
    }

}
