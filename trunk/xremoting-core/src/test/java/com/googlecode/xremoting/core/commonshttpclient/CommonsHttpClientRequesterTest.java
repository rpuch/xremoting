package com.googlecode.xremoting.core.commonshttpclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.test.QAUtils;

public class CommonsHttpClientRequesterTest {
	
	@Test
	public void test() throws Exception {
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
}
