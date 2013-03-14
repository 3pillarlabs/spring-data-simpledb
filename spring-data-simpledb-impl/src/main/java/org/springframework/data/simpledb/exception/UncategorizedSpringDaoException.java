package org.springframework.data.simpledb.exception;

import org.springframework.dao.UncategorizedDataAccessException;

public class UncategorizedSpringDaoException extends UncategorizedDataAccessException {
    public UncategorizedSpringDaoException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
