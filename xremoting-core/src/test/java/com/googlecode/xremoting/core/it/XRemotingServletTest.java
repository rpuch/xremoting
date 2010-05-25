package com.googlecode.xremoting.core.it;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.googlecode.xremoting.core.XRemotingProxyFactory;
import com.googlecode.xremoting.core.exception.InvokedSideInvocationException;
import com.googlecode.xremoting.core.http.DefaultHttpConnectionFactory;
import com.googlecode.xremoting.core.http.HttpConnectionFactory;
import com.googlecode.xremoting.core.http.HttpRequester;
import com.googlecode.xremoting.core.servlet.XRemotingServlet;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.test.A;
import com.googlecode.xremoting.core.test.ABImpl;
import com.googlecode.xremoting.core.test.B;
import com.googlecode.xremoting.core.test.CoolService;
import com.googlecode.xremoting.core.test.CoolServiceInterface;

public class XRemotingServletTest {
	
	private static final int EMBEDDED_SERVER_PORT = 8867;
	
	private Server server;
	
	private CoolServiceInterface coolServiceImpl = new CoolService();
	private ABImpl abImpl = new ABImpl();
	
	@Before
	public void setUp() throws Exception {
		server = new Server(EMBEDDED_SERVER_PORT);
		Context root = new Context(server, "/" ,Context.SESSIONS);
		XRemotingServlet coolServiceServlet = new XRemotingServlet() {
			private static final long serialVersionUID = 4705072705335841313L;

			@Override
			protected Object getTarget() {
				return coolServiceImpl;
			}
		};
		ServletHolder coolServiceServletHolder = new ServletHolder(coolServiceServlet);
		coolServiceServletHolder.setInitParameter("exposedInterfaces", CoolServiceInterface.class.getName());
		root.addServlet(coolServiceServletHolder, "/cool-service");
		
		XRemotingServlet aServlet = new XRemotingServlet() {
			private static final long serialVersionUID = 4705072705335841313L;

			@Override
			protected Object getTarget() {
				return abImpl;
			}
		};
		ServletHolder aServletHolder = new ServletHolder(aServlet);
		aServletHolder.setInitParameter("exposedInterfaces", A.class.getName());
		root.addServlet(aServletHolder, "/a");
		
		XRemotingServlet abServlet = new XRemotingServlet() {
			private static final long serialVersionUID = 4705072705335841313L;

			@Override
			protected Object getTarget() {
				return abImpl;
			}
		};
		ServletHolder abServletHolder = new ServletHolder(abServlet);
		abServletHolder.setInitParameter("exposedInterfaces", A.class.getName() + "," + B.class.getName());
		root.addServlet(abServletHolder, "/ab");
		
		server.start();
	}
	
	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.stop();
		}
	}

	@Test
	public void testInvoke() throws Exception {
		XRemotingProxyFactory factory = createProxyFactory("/cool-service");
		CoolServiceInterface coolService = (CoolServiceInterface) factory.create(CoolServiceInterface.class);
		String result = coolService.doCoolStuff("abc", 9, -13, new Class<?>[]{CoolService.class, String.class});
		Assert.assertEquals("abc9-13CoolService", result);
	}
	
	@Test
	public void testInvokeOnMultipleInterfacesViaCorrectInterface() throws Exception {
		XRemotingProxyFactory factory = createProxyFactory("/a");
		A a = (A) factory.create(A.class);
		a.a();
	}
	
	@Test
	public void testInvokeOnMultipleInterfacesViaWrongInterface() throws Exception {
		XRemotingProxyFactory factory = createProxyFactory("/a");
		B b = (B) factory.create(B.class);
		try {
			b.b();
			Assert.fail();
		} catch (InvokedSideInvocationException e) {
			// expected
		}
	}
	
	@Test
	public void testInvokeOnMultipleInterfacesViaBothInterfaces() throws Exception {
		XRemotingProxyFactory factory = createProxyFactory("/ab");
		Object proxy = factory.create(new Class<?>[]{A.class, B.class});
		A a = (A) proxy;
		B b = (B) proxy;
		a.a();
		b.b();
	}

	private XRemotingProxyFactory createProxyFactory(String uri) {
		HttpConnectionFactory httpConnectionFactory = new DefaultHttpConnectionFactory();
		Requester requester = new HttpRequester(httpConnectionFactory, "http://localhost:" + EMBEDDED_SERVER_PORT + uri);
		XRemotingProxyFactory factory = new XRemotingProxyFactory(requester);
		return factory;
	}
}
