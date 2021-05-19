package com.revature.exceptions;

public class WrongPassword extends RuntimeException {
	private static final long serialVersionUID = 5192391238939983638L;

	public WrongPassword() {
		super();
	}

	public WrongPassword(String message) {
		super(message);
	}
}
