package com.googlecode.xremoting.core.it;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

import com.googlecode.xremoting.core.XRemotingProxyFactory;
import com.googlecode.xremoting.core.exception.InvokedSideInvocationException;
import com.googlecode.xremoting.core.http.DefaultHttpConnectionFactory;
import com.googlecode.xremoting.core.http.HttpConnectionFactory;
import com.googlecode.xremoting.core.http.HttpRequester;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.test.A;
import com.googlecode.xremoting.core.test.ABImpl;
import com.googlecode.xremoting.core.test.B;
import com.googlecode.xremoting.core.test.CoolService;
import com.googlecode.xremoting.core.test.CoolServiceInterface;
import com.googlecode.xremoting.core.test.QAUtils;

public class XRemotingServletTest {
	
	private Server server;
	
	private CoolServiceInterface coolServiceImpl = new CoolService();
	private ABImpl abImpl = new ABImpl();
	
	@Before
	public void setUp() throws Exception {
		server = QAUtils.createJettyServer();
		Context root = QAUtils.createContext(server);
		
		QAUtils.addServiceServlet(root, coolServiceImpl, CoolServiceInterface.class.getName(), "/cool-service");
		QAUtils.addServiceServlet(root, abImpl, A.class.getName(), "/a");
		QAUtils.addServiceServlet(root, abImpl, A.class.getName() + "," + B.class.getName(), "/ab");

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
	public void testInvokeOnSimpleFactory() throws Exception {
		XRemotingProxyFactory factory = createSimpleProxyFactory("/cool-service");
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
		Requester requester = new HttpRequester(httpConnectionFactory, QAUtils.buildUrl(uri));
		XRemotingProxyFactory factory = new XRemotingProxyFactory(requester);
		return factory;
	}
	
	private XRemotingProxyFactory createSimpleProxyFactory(String uri) {
		return new XRemotingProxyFactory(QAUtils.buildUrl(uri));
	}
}
