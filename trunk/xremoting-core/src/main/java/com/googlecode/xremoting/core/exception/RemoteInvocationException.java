package com.googlecode.xremoting.core.exception;

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
