package org.springframework.data.simpledb.exception;

import org.springframework.dao.UncategorizedDataAccessException;

public class UncategorizedSpringDaoException extends UncategorizedDataAccessException {

	private static final long serialVersionUID = 1L;

	public UncategorizedSpringDaoException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
