package com.googlecode.xremoting.core.exception;

/**
 * Any problem which occurred during a remote invocation.
 * 
 * @author Roman Puchkovskiy
 */
public class RemoteInvocationException extends XRemotingException {
	private static final long serialVersionUID = -5342939014996411402L;

	public RemoteInvocationException() {
		super();
	}

	public RemoteInvocationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RemoteInvocationException(String message) {
		super(message);
	}

	public RemoteInvocationException(Throwable cause) {
		super(cause);
	}

}
