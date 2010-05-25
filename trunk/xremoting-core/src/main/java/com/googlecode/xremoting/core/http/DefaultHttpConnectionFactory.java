package com.googlecode.xremoting.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DefaultHttpConnectionFactory implements HttpConnectionFactory {
	
	public HttpURLConnection openConnection(String url) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		return connection;
	}

}
