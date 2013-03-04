package org.springframework.data.simpledb.core;

public class InvalidSimpleDBQueryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidSimpleDBQueryException(String message, Throwable e) {
		super(message, e);
	}
}
