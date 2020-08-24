package com.revature.exceptions;

public class AccountMismatchException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AccountMismatchException() {
		super();
	}
	
	public AccountMismatchException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AccountMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccountMismatchException(String message) {
		super(message);
	}

	public AccountMismatchException(Throwable cause) {
		super(cause);
	}
}
