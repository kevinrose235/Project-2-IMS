package com.revature.exceptions;

public class NoStoreFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoStoreFoundException(String message) {
		super(message);
	}
}
