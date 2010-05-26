package com.googlecode.xremoting.core.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.test.TestUtils;

public class HttpRequesterTest {
	
	@Test
	public void test() throws Exception {
		Server server = TestUtils.createJettyServer();
		Context root = TestUtils.createContext(server);
		TestUtils.addHelloServlet(root);
		try {
			server.start();
			
			HttpConnectionFactory factory = new DefaultHttpConnectionFactory();
			Requester requester = new HttpRequester(factory, TestUtils.buildUrl("/test-servlet"));
			Request request = requester.createRequest();
			request.getOutputStream().write("Hello server!".getBytes("utf-8"));
			request.commitRequest();
			String line = new BufferedReader(new InputStreamReader(request.getInputStream())).readLine();
			Assert.assertEquals("Hello client!", line);
		} finally {
			server.stop();
		}
	}
}
