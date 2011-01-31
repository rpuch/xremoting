package com.googlecode.xremoting.core.spi;

import java.io.IOException;

/**
 * <p>
 * Central interface for transports. Used as a factory for requests.
 * </p>
 * <p>
 * To add a new transport/library support, implement this interface as long as
 * {@link Request}.
 * </p>
 * 
 * @author Roman Puchkovskiy
 * @see Request
 */
public interface Requester {
	/**
	 * Creates and initializes a new request.
	 * 
	 * @return request
	 * @throws IOException	if input/output error occurs
	 */
	Request createRequest() throws IOException;
}
