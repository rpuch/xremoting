package com.googlecode.xremoting.core.exception;

public class InitializationException extends XRemotingException {
	private static final long serialVersionUID = -1674018736647004543L;

	public InitializationException() {
	}

	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializationException(String message) {
		super(message);
	}

	public InitializationException(Throwable cause) {
		super(cause);
	}

}
