package com.googlecode.xremoting.core.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.junit.Assert;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

import com.googlecode.xremoting.core.test.QAUtils;

public class DefaultHttpConnectionFactoryTest {
	
	@Test
	public void test() throws Exception {
		Server server = QAUtils.createJettyServer();
		Context root = QAUtils.createContext(server);
		QAUtils.addHelloServlet(root);
		try {
			server.start();
			DefaultHttpConnectionFactory factory = new DefaultHttpConnectionFactory();
			HttpURLConnection connection = factory.openConnection(QAUtils.buildUrl("/test-servlet"));
			connection.getOutputStream().write("Hello server!".getBytes("utf-8"));
			String line = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
			connection.disconnect();
			Assert.assertEquals("Hello client!", line);
		} finally {
			server.stop();
		}
	}
}
