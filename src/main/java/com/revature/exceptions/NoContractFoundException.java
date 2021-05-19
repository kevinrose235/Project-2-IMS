package com.revature.exceptions;

public class NoContractFoundException extends RuntimeException {
	private static final long serialVersionUID = -3475063787467080763L;

	public NoContractFoundException(String message) {
		super(message);
	}

}
