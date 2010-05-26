package com.googlecode.xremoting.core.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class TestUtils {
	
	private static final int EMBEDDED_SERVER_PORT = 8867;
	
	public static Server createJettyServer() {
		Server server = new Server(EMBEDDED_SERVER_PORT);
		return server;
	}
	
	public static Context createContext(Server server) {
		Context root = new Context(server, "/", Context.SESSIONS);
		return root;
	}
	
	public static String buildUrl(String uri) {
		return "http://localhost:" + EMBEDDED_SERVER_PORT + uri;
	}
	
	public static void addHelloServlet(Context root) {
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
	}
}
