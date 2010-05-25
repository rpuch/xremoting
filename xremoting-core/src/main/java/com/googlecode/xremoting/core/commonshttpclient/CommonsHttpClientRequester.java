package com.googlecode.xremoting.core.commonshttpclient;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;

public class CommonsHttpClientRequester implements Requester {
	
	private HttpClient httpClient;
	private String url;

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
