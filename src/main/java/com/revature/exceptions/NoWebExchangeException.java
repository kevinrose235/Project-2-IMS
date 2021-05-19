package com.revature.exceptions;

public class NoWebExchangeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoWebExchangeException(String string) {
		super(string);
	}
}
