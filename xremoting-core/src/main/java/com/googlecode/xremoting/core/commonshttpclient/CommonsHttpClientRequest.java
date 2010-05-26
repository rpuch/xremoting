package com.googlecode.xremoting.core.commonshttpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import com.googlecode.xremoting.core.spi.Request;

/**
 * Implementation of {@link Request} for commons-httpclient.
 * 
 * @author Roman Puchkovskiy
 * @see CommonsHttpClientRequester
 */
public class CommonsHttpClientRequest implements Request {

	private HttpClient httpClient;
	private PostMethod method;
	private ByteArrayOutputStream funnel = new ByteArrayOutputStream();
	private boolean committed = false;
	private OutputStream os = new FunnelOutputStream();
	private InputStream is;
	
	public CommonsHttpClientRequest(HttpClient httpClient, PostMethod method) {
		super();
		this.httpClient = httpClient;
		this.method = method;
	}

	public OutputStream getOutputStream() throws IOException {
		return os;
	}

	public void commitRequest() throws IOException {
		checkNotCommitted();
		committed = true;
		method.setRequestEntity(new ByteArrayRequestEntity(funnel.toByteArray()));
		funnel = null;
		try {
			os.close();
		} catch (IOException e) {
			// ignore
		}
		os = null;
		httpClient.executeMethod(method);
		is = method.getResponseBodyAsStream();
	}

	public InputStream getInputStream() throws IOException {
		checkCommitted();
		return is;
	}

	public void releaseRequest() {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				// ignore
			}
			is = null;
		}
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				// ignore
			}
			os = null;
		}
		funnel = null;
		if (method != null) {
			method.releaseConnection();
		}
	}
	
	private void checkNotCommitted() {
		if (committed) {
			throw new IllegalStateException("Already committed!");
		}
	}
	
	private void checkCommitted() {
		if (!committed) {
			throw new IllegalStateException("Not yet committed!");
		}
	}
	
	private class FunnelOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			checkNotCommitted();
			funnel.write(b);
		}
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			checkNotCommitted();
			funnel.write(b, off, len);
		}
		@Override
		public void write(byte[] b) throws IOException {
			checkNotCommitted();
			funnel.write(b);
		}
	}

}
