package com.googlecode.xremoting.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;

/**
 * {@link Requester} which uses HTTP as a transport protocol and
 * {@link HttpURLConnection} to work with it. HttpConnectionFactory
 * implementation is the one who actually opens connections.
 * 
 * @author Roman Puchkovskiy
 * @see HttpConnectionFactory
 * @see DefaultHttpConnectionFactory
 */
public class HttpRequester implements Requester {
	
	private HttpConnectionFactory httpConnectionFactory;
	private String url;

	/**
	 * Creates a new instance using the supplied {@link HttpConnectionFactory}
	 * and URL of the service available via HTTP(s).
	 * 
	 * @param httpConnectionFactory	factory to get connections from
	 * @param url					service URL
	 */
	public HttpRequester(HttpConnectionFactory httpConnectionFactory, String url) {
		super();
		this.httpConnectionFactory = httpConnectionFactory;
		this.url = url;
	}

	public Request createRequest() throws IOException {
		return new HttpRequest(httpConnectionFactory.openConnection(url));
	}

}
