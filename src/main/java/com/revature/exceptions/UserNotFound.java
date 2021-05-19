package com.revature.exceptions;

public class UserNotFound extends RuntimeException {
	private static final long serialVersionUID = 8102706327905923076L;

	public UserNotFound() {
		super();
	}

	public UserNotFound(String message) {
		super(message);
	}


}
