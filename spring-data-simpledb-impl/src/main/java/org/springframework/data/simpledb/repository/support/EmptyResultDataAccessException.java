package org.springframework.data.simpledb.repository.support;

// TODO: use defined simpledb exceptions
public class EmptyResultDataAccessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmptyResultDataAccessException(String message) {
		super(message);
	}

	public EmptyResultDataAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
