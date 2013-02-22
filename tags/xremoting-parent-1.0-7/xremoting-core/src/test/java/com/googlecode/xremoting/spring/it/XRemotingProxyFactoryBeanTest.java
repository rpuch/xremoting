package com.googlecode.xremoting.spring.it;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.remoting.RemoteAccessException;

import com.googlecode.xremoting.core.test.A;
import com.googlecode.xremoting.core.test.ABImpl;
import com.googlecode.xremoting.core.test.B;
import com.googlecode.xremoting.core.test.CoolService;
import com.googlecode.xremoting.core.test.CoolServiceInterface;
import com.googlecode.xremoting.core.test.QAUtils;

public class XRemotingProxyFactoryBeanTest {
	private Server server;
	private ApplicationContext applicationContext;
	
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
		
		applicationContext = new ClassPathXmlApplicationContext("classpath:proxy-factory-test-context.xml");
	}
	
	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.stop();
		}
		if (applicationContext != null) {
			applicationContext = null;
		}
	}

	@Test
	public void testInvoke() throws Exception {
		CoolServiceInterface coolService = (CoolServiceInterface) applicationContext.getBean("coolService");
		String result = coolService.doCoolStuff("abc", 9, -13, new Class<?>[]{CoolService.class, String.class});
		Assert.assertEquals("abc9-13CoolService", result);
	}
	
	@Test
	public void testInvokeOnMultipleInterfacesViaCorrectInterface() throws Exception {
		A a = (A) applicationContext.getBean("a");
		a.a();
	}
	
	@Test
	public void testInvokeOnMultipleInterfacesViaWrongInterface() throws Exception {
		B b = (B) applicationContext.getBean("b");
		try {
			b.b();
			Assert.fail();
		} catch (RemoteAccessException e) {
			// expected
		}
	}
	
	@Test
	public void testInvokeOnMultipleInterfacesViaBothInterfaces() throws Exception {
		A a = (A) applicationContext.getBean("ab_a");
		B b = (B) applicationContext.getBean("ab_b");
		a.a();
		b.b();
	}

}
