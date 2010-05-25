package com.googlecode.xremoting.core.spi;

public class SerializationException extends Exception {
	private static final long serialVersionUID = 8886919922737491350L;

	public SerializationException() {
	}

	public SerializationException(String message) {
		super(message);
	}

	public SerializationException(Throwable cause) {
		super(cause);
	}

	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
