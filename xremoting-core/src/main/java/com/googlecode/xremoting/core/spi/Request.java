package com.googlecode.xremoting.core.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Request {
	OutputStream getOutputStream() throws IOException;
	void commitRequest() throws IOException;
	InputStream getInputStream() throws IOException;
	void releaseRequest();
}
