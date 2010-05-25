package com.googlecode.xremoting.core.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class DefaultHttpConnectionFactoryTest {
	
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
			DefaultHttpConnectionFactory factory = new DefaultHttpConnectionFactory();
			HttpURLConnection connection = factory.openConnection("http://localhost:" + EMBEDDED_SERVER_PORT + "/test-servlet");
			connection.getOutputStream().write("Hello server!".getBytes("utf-8"));
			String line = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
			connection.disconnect();
			Assert.assertEquals("Hello client!", line);
		} finally {
			server.stop();
		}
	}
}
