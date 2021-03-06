package com.googlecode.xremoting.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link HttpConnectionFactory} implementation which uses URL#openConnection().
 * 
 * @author Roman Puchkovskiy
 */
public class DefaultHttpConnectionFactory implements HttpConnectionFactory {
	
	public HttpURLConnection openConnection(String url) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		configureConnection(connection);
		return connection;
	}

	protected void configureConnection(HttpURLConnection connection) {
		connection.setRequestProperty("Content-Type", "application/xml");
	}

}
