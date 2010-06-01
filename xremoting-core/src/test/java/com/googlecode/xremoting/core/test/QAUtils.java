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

import com.googlecode.xremoting.core.servlet.XRemotingServlet;

public class QAUtils {
	
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
	
	public static void addRedirectingServlet(Context root) {
		root.addServlet(new ServletHolder(new HttpServlet() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void doPost(HttpServletRequest req,
					HttpServletResponse resp) throws ServletException,
					IOException {
				resp.sendRedirect("/some/location");
			}
			
		}), "/redirecting-servlet");
	}
	
	public static void addServiceServlet(Context root, final Object target,
			String exposedInterfaces, String uri) {
		XRemotingServlet servlet = new XRemotingServlet() {
			private static final long serialVersionUID = 4705072705335841313L;

			@Override
			protected Object getTarget() {
				return target;
			}
		};
		ServletHolder servletHolder = new ServletHolder(servlet);
		servletHolder.setInitParameter("exposedInterfaces", exposedInterfaces);
		root.addServlet(servletHolder, uri);
	}
}
