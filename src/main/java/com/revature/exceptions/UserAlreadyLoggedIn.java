package com.revature.exceptions;

public class UserAlreadyLoggedIn extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UserAlreadyLoggedIn(String message) {
		super(message);
	}
}
