package com.revature.exceptions;

public class InvalidDataException extends RuntimeException {
	private static final long serialVersionUID = 194091640648198875L;

	public InvalidDataException() {
		super();
	}

	public InvalidDataException(String message) {
		super(message);
	}
}
