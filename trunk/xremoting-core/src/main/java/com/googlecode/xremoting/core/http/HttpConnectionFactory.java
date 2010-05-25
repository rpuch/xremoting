package com.googlecode.xremoting.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * Factory for {@link HttpURLConnection} instances.
 * 
 * @author Roman Puchkovskiy
 */
public interface HttpConnectionFactory {
	/**
	 * Opens a new connection.
	 * 
	 * @param url	URL to which to open connection via HTTP/HTTPS.
	 * @return connection
	 * @throws MalformedURLException	if URL is not correct
	 * @throws IOException				if any other error occurs (like no
	 * route to host)
	 */
	HttpURLConnection openConnection(String url) throws MalformedURLException, IOException;
}
