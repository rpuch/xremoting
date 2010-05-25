package com.googlecode.xremoting.core.commonshttpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;

public class CommonsHttpClientRequesterTest {
	
	private static final int EMBEDDED_SERVER_PORT = 8867;
	
	@Test
	public void test() throws Exception {
		Server server = new Server(EMBEDDED_SERVER_PORT);
		Context root = new Context(server, "/" ,Context.SESSIONS);
		root.addServlet(new ServletHolder(new HttpServlet() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void doPost(HttpServletRequest req,
					HttpServletResponse resp) throws ServletException,
					IOException {
				String line = req.getReader().readLine();
				Assert.assertEquals("Hello server!", line);
				resp.getWriter().write("Hello client!");
				resp.getWriter().close();
			}
			
		}), "/test-servlet");
		try {
			server.start();
			
			Requester requester = new CommonsHttpClientRequester(new HttpClient(), "http://localhost:" + EMBEDDED_SERVER_PORT + "/test-servlet");
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
