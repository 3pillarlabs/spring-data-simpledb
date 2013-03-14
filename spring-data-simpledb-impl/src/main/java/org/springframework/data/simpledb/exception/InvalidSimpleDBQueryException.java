package org.springframework.data.simpledb.exception;

import org.springframework.dao.UncategorizedDataAccessException;

public class InvalidSimpleDBQueryException extends UncategorizedDataAccessException{

	private static final long serialVersionUID = 1L;

	public InvalidSimpleDBQueryException(String message, Throwable e) {
		super(message, e);
	}
}
