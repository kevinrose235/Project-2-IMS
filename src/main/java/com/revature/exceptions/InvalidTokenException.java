package com.revature.exceptions;

public class InvalidTokenException extends RuntimeException {
	public InvalidTokenException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}
