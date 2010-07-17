package com.googlecode.xremoting.core.commonshttpclient;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;

/**
 * Implementation of {@link Requester} for commons-httpclient. Needs a
 * configured {@link HttpClient} instance.
 * 
 * @author Roman Puchkovskiy
 */
public class CommonsHttpClientRequester implements Requester {
	
	private HttpClient httpClient;
	private String url;

	/**
	 * Creates a new CommonsHttpClientRequester instance using a pre-configured
	 * HttpClient and URL of remote service which is accessible using HTTP(s).
	 * 
	 * @param httpClient	HttpClient instance
	 * @param url			remote service URL
	 */
	public CommonsHttpClientRequester(HttpClient httpClient, String url) {
		super();
		this.httpClient = httpClient;
		this.url = url;
	}

	@Override
	public Request createRequest() throws IOException {
		PostMethod method = new PostMethod(url);
		return new CommonsHttpClientRequest(httpClient, method);
	}

}
