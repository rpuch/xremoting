package com.googlecode.xremoting.core.http;

import java.io.IOException;

import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;

public class HttpRequester implements Requester {
	
	private HttpConnectionFactory httpConnectionFactory;
	private String url;
	
	public HttpRequester(HttpConnectionFactory httpConnectionFactory, String url) {
		super();
		this.httpConnectionFactory = httpConnectionFactory;
		this.url = url;
	}

	public Request createRequest() throws IOException {
		return new HttpRequest(httpConnectionFactory.openConnection(url));
	}

}
