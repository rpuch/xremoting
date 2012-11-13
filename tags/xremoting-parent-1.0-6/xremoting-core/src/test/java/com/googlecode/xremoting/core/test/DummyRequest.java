/**
 * 
 */
package com.googlecode.xremoting.core.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.xremoting.core.spi.Request;

public class DummyRequest implements Request {
	private boolean released = false;
	
	public void commitRequest() throws IOException {
	}

	public InputStream getInputStream() throws IOException {
		return null;
	}

	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	public void releaseRequest() {
		released = true;
	}
	
	public boolean isReleased() {
		return released;
	}
}