package com.googlecode.xremoting.core.spi;

import java.io.IOException;

public interface Requester {
	Request createRequest() throws IOException;
}
