package com.googlecode.xremoting.core.exception;

/**
 * Represents a problem during remote invocation which was detected on the
 * invoking side (i.e. on the client).
 * 
 * @author Roman Puchkovskiy
 */
public class InvokingSideInvocationException extends RemoteInvocationException {
	private static final long serialVersionUID = -8426703116163317100L;

	public InvokingSideInvocationException() {
	}

	public InvokingSideInvocationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvokingSideInvocationException(String message) {
		super(message);
	}

	public InvokingSideInvocationException(Throwable cause) {
		super(cause);
	}

}
