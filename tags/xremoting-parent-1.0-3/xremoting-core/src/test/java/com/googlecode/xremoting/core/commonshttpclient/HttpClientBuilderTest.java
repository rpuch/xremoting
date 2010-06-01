package com.googlecode.xremoting.core.commonshttpclient;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.googlecode.xremoting.core.test.QAUtils;

public class HttpClientBuilderTest {
	
	private Server server;
	
	@Before
	public void setUp() throws Exception {
		server = QAUtils.createJettyServer();
		Context root = QAUtils.createContext(server);
		root.addServlet(new ServletHolder(new HttpServlet() {
			private static final long serialVersionUID = -303205245596407931L;

			@Override
			protected void doGet(HttpServletRequest req,
					HttpServletResponse resp) throws ServletException,
					IOException {
				String auth = req.getHeader("Authorization");
				String expected = new String(Base64.encodeBase64("user:password".getBytes("utf-8")), "utf-8");
				if (("Basic " + expected).equals(auth)) {
					resp.getWriter().print("ok");
					resp.getWriter().close();
				} else {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
				}
			}
		}), "/basic");
		server.start();
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.stop();
		}
	}
	
	@Test
	public void testBasicAuth() throws Exception {
		HttpClient httpClient;
		GetMethod method;

		httpClient = HttpClientBuilder.create().basicAuth("user", "password").build();
		method = null;
		try {
			method = new GetMethod(QAUtils.buildUrl("/basic"));
			httpClient.executeMethod(method);
			String resp = method.getResponseBodyAsString();
			Assert.assertEquals("ok", resp);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}

		httpClient = HttpClientBuilder.create().build();
		method = null;
		try {
			method = new GetMethod(QAUtils.buildUrl("/basic"));
			httpClient.executeMethod(method);
			int code = method.getStatusCode();
			Assert.assertEquals(403, code);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	@Test
	public void testConfigProxy() throws Exception {
		HttpClientBuilder.create().proxyHost("code.google.com").build();
	}
}
