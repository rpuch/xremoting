package com.googlecode.xremoting.spring;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.googlecode.xremoting.core.test.CoolServiceInterface;

public class XRemotingClientInterceptorTest {
	@Test
	public void testConfig() throws Exception {
		XRemotingClientInterceptor interceptor = createEmptyInterceptor();
		interceptor.afterPropertiesSet();
	}
	
	@Test
	public void testBasic() throws Exception {
		XRemotingClientInterceptor interceptor = createEmptyInterceptor();
		interceptor.setUsername("username");
		interceptor.setPassword("password");
		interceptor.afterPropertiesSet();
	}
	
	@Test
	public void testSslUrls() throws Exception {
		XRemotingClientInterceptor interceptor;
		interceptor = createEmptyInterceptor();
		interceptor.setTrustKeyStoreUrl(getClass().getClassLoader().getResource("remoting-service-exporter-test-context.xml"));
		interceptor.setKeyStoreUrl(getClass().getClassLoader().getResource("remoting-service-exporter-test-context.xml"));
		interceptor.setTrustKeyStorePassword("password");
		interceptor.setKeyStorePassword("password");
		interceptor.setSslHost("code.google.com");
		interceptor.afterPropertiesSet();
	}
	
	@Test
	public void testSslResources() throws Exception {
		XRemotingClientInterceptor interceptor;
		interceptor = createEmptyInterceptor();
		interceptor.setTrustKeyStoreResource(new ClassPathResource("remoting-service-exporter-test-context.xml"));
		interceptor.setKeyStoreResource(new ClassPathResource("remoting-service-exporter-test-context.xml"));
		interceptor.setTrustKeyStorePassword("password");
		interceptor.setKeyStorePassword("password");
		interceptor.setSslHost("code.google.com");
		interceptor.afterPropertiesSet();
	}
		
	private XRemotingClientInterceptor createEmptyInterceptor() {
		XRemotingClientInterceptor interceptor = new XRemotingClientInterceptor();
		interceptor.setServiceUrl("http://code.google.com");
		interceptor.setServiceInterface(CoolServiceInterface.class);
		return interceptor;
	}
}
