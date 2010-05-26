package com.googlecode.xremoting.spring.it;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.googlecode.xremoting.core.XRemotingProxyFactory;
import com.googlecode.xremoting.core.http.DefaultHttpConnectionFactory;
import com.googlecode.xremoting.core.http.HttpConnectionFactory;
import com.googlecode.xremoting.core.http.HttpRequester;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.test.CoolServiceInterface;
import com.googlecode.xremoting.core.test.QAUtils;

public class XRemotingServiceExporterTest {
	
	private Server server;
	private CoolServiceInterface coolService;
	
	@Before
	public void setUp() throws Exception {
		server = QAUtils.createJettyServer();
		Context root = QAUtils.createContext(server);
		
		XmlWebApplicationContext wac = new XmlWebApplicationContext();
		wac.setServletContext(root.getServletContext());
		wac.setConfigLocations(new String[]{"classpath:remoting-service-exporter-test-context.xml"});
		wac.refresh();
		
		root.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		
		ServletHolder servletHolder = new ServletHolder(HttpRequestHandlerServlet.class);
		servletHolder.setName("xremotingExporter");
		root.addServlet(servletHolder, "/spring-exporter");
		
		server.start();

		HttpConnectionFactory httpConnectionFactory = new DefaultHttpConnectionFactory();
		Requester requester = new HttpRequester(httpConnectionFactory, QAUtils.buildUrl("/spring-exporter"));
		XRemotingProxyFactory factory = new XRemotingProxyFactory(requester);
		coolService = (CoolServiceInterface) factory.create(CoolServiceInterface.class);
	}
	
	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.stop();
		}
		coolService = null;
	}

	@Test
	public void testInvoke() throws Exception {
		String res = coolService.doCoolStuff("abc", 4, -15, new Class<?>[]{Class.class, Integer.class});
		Assert.assertEquals("abc4-15Class", res);
	}
	
	@Test
	public void testThrow() throws Exception {
		try {
			coolService.throwing();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(Exception.class, e.getClass());
			Assert.assertEquals("Just thrown", e.getMessage());
		}
	}
}
