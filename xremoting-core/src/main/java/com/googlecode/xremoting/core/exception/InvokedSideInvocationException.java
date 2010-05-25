package com.googlecode.xremoting.core.exception;

public class InvokedSideInvocationException extends RemoteInvocationException {
	private static final long serialVersionUID = 973057665348130098L;

	public InvokedSideInvocationException() {
	}

	public InvokedSideInvocationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvokedSideInvocationException(String message) {
		super(message);
	}

	public InvokedSideInvocationException(Throwable cause) {
		super(cause);
	}

}
