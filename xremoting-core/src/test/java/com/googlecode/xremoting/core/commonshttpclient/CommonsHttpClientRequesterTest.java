package com.googlecode.xremoting.core.commonshttpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

import com.googlecode.xremoting.core.http.DefaultHttpConnectionFactory;
import com.googlecode.xremoting.core.http.HttpConnectionFactory;
import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.test.QAUtils;

public class CommonsHttpClientRequesterTest {
	
	@Test
	public void testSuccess() throws Exception {
		Server server = QAUtils.createJettyServer();
		Context root = QAUtils.createContext(server);
		QAUtils.addHelloServlet(root);
		try {
			server.start();
			
			Requester requester = new CommonsHttpClientRequester(new HttpClient(), QAUtils.buildUrl("/test-servlet"));
			Request request = requester.createRequest();
			request.getOutputStream().write("Hello server!".getBytes("utf-8"));
			request.commitRequest();
			String line = new BufferedReader(new InputStreamReader(request.getInputStream())).readLine();
			Assert.assertEquals("Hello client!", line);
		} finally {
			server.stop();
		}
	}
	
	@Test
	public void testRedirect() throws Exception {
		Server server = QAUtils.createJettyServer();
		Context root = QAUtils.createContext(server);
		QAUtils.addRedirectingServlet(root);
		try {
			server.start();
			
			HttpConnectionFactory factory = new DefaultHttpConnectionFactory();
			Requester requester = new CommonsHttpClientRequester(new HttpClient(), QAUtils.buildUrl("/redirecting-servlet"));
			Request request = requester.createRequest();
			request.commitRequest();
			Assert.fail();
		} catch (IOException e) {
			Assert.assertTrue(e.getMessage().contains("200"));
		} finally {
			server.stop();
		}
	}
}
