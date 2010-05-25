package com.googlecode.xremoting.core.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.googlecode.xremoting.core.spi.Request;

public class HttpRequest implements Request {
	
	private HttpURLConnection connection;

	public HttpRequest(HttpURLConnection connection) {
		this.connection = connection;
	}
	
	public OutputStream getOutputStream() throws IOException {
		return connection.getOutputStream();
	}
	
	public void commitRequest() throws IOException {
		connection.getOutputStream().flush();
	}
	
	public InputStream getInputStream() throws IOException {
		return connection.getInputStream();
	}

	public void releaseRequest() {
		connection.disconnect();
	}

}
