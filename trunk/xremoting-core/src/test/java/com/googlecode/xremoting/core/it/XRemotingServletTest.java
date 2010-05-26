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
import com.googlecode.xremoting.core.test.TestUtils;

public class XRemotingServletTest {
	
	private static final int EMBEDDED_SERVER_PORT = 8867;
	
	private Server server;
	
	private CoolServiceInterface coolServiceImpl = new CoolService();
	private ABImpl abImpl = new ABImpl();
	
	@Before
	public void setUp() throws Exception {
		server = TestUtils.createJettyServer();
		Context root = TestUtils.createContext(server);
		
		addServiceServlet(root, coolServiceImpl, CoolServiceInterface.class.getName(), "/cool-service");
		addServiceServlet(root, abImpl, A.class.getName(), "/a");
		addServiceServlet(root, abImpl, A.class.getName() + "," + B.class.getName(), "/ab");

		server.start();
	}

	private void addServiceServlet(Context root, final Object target,
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
