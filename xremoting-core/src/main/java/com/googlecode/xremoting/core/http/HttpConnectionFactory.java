package com.googlecode.xremoting.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public interface HttpConnectionFactory {
	HttpURLConnection openConnection(String url) throws MalformedURLException, IOException;
}
