package com.googlecode.xremoting.core.exception;

public class XRemotingException extends RuntimeException {
	private static final long serialVersionUID = 8322295140849016512L;

	public XRemotingException() {
		super();
	}

	public XRemotingException(String message, Throwable cause) {
		super(message, cause);
	}

	public XRemotingException(String message) {
		super(message);
	}

	public XRemotingException(Throwable cause) {
		super(cause);
	}

}
