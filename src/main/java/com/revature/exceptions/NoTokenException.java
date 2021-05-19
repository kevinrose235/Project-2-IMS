package com.revature.exceptions;

public class NoTokenException extends RuntimeException {
	public NoTokenException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;
}
