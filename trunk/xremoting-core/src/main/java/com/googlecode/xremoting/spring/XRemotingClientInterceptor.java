package com.googlecode.xremoting.spring;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.httpclient.HttpClient;
import org.springframework.core.io.Resource;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.remoting.caucho.BurlapClientInterceptor;
import org.springframework.remoting.caucho.HessianClientInterceptor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.googlecode.xremoting.core.XRemotingProxyFactory;
import com.googlecode.xremoting.core.commonshttpclient.CommonsHttpClientRequester;
import com.googlecode.xremoting.core.commonshttpclient.HttpClientBuilder;
import com.googlecode.xremoting.core.exception.XRemotingException;
import com.googlecode.xremoting.core.http.DefaultHttpConnectionFactory;
import com.googlecode.xremoting.core.http.HttpConnectionFactory;
import com.googlecode.xremoting.core.http.HttpRequester;
import com.googlecode.xremoting.core.spi.Requester;

/**
 * Client interceptor for XRemoting to use for remote calls.
 * 
 * @author Roman Puchkovskiy
 * @see HttpInvokerClientInterceptor
 * @see HessianClientInterceptor
 * @see BurlapClientInterceptor
 */
public class XRemotingClientInterceptor extends UrlBasedRemoteAccessor
		implements MethodInterceptor {
	
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
	private Requester requester = null;
	private Object httpClient = null;
	
	private String username;
	private String password;
	private boolean authPreemptive = true;
	private String basicAuthHost;
	private int basicAuthPort = -1;
	
	private URL trustKeyStoreUrl;
	private Resource trustKeyStoreResource;
	private URL keyStoreUrl;
	private Resource keyStoreResource;
	private String trustKeyStorePassword;
	private String keyStorePassword;
	private String sslHost;
	private int sslPort = 443;
	
	private Object xremotingProxy;
	
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}
	
	public void setRequester(Requester requester) {
		this.requester = requester;
	}

	public void setHttpClient(Object httpClient) {
		this.httpClient = httpClient;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthPreemptive(boolean authPreemptive) {
		this.authPreemptive = authPreemptive;
	}

	public void setBasicAuthHost(String basicAuthHost) {
		this.basicAuthHost = basicAuthHost;
	}

	public void setBasicAuthPort(int basicAuthPort) {
		this.basicAuthPort = basicAuthPort;
	}

	public void setTrustKeyStoreUrl(URL trustKeyStoreUrl) {
		this.trustKeyStoreUrl = trustKeyStoreUrl;
	}

	public void setTrustKeyStoreResource(Resource trustKeyStoreResource) {
		this.trustKeyStoreResource = trustKeyStoreResource;
	}

	public void setKeyStoreUrl(URL keyStoreUrl) {
		this.keyStoreUrl = keyStoreUrl;
	}

	public void setKeyStoreResource(Resource keyStoreResource) {
		this.keyStoreResource = keyStoreResource;
	}

	public void setTrustKeyStorePassword(String trustKeyStorePassword) {
		this.trustKeyStorePassword = trustKeyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public void setSslHost(String sslHost) {
		this.sslHost = sslHost;
	}

	public void setSslPort(int sslPort) {
		this.sslPort = sslPort;
	}

	protected final ClassLoader getBeanClassLoader() {
		return this.beanClassLoader;
	}
	
	public void afterPropertiesSet() {
		Assert.notNull(getServiceInterface(), "serviceInterface cannot be null");
		super.afterPropertiesSet();
		try {
			prepare();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void prepare() throws IOException {
		this.xremotingProxy = createXRemotingProxy(createProxyFactory());
	}
		
	protected XRemotingProxyFactory createProxyFactory() throws IOException {
		if (requester == null) {
			requester = createRequester();
		}
		return new XRemotingProxyFactory(requester);
	}

	protected Requester createRequester() throws IOException {
		if (username != null
				|| trustKeyStoreResource != null || trustKeyStoreUrl != null
				|| keyStoreResource != null || keyStoreUrl != null) {
			return createComplexRequester();
		} else {
			return createSimpleRequester();
		}
	}

	protected Requester createSimpleRequester() {
		HttpConnectionFactory httpConnectionFactory = createHttpConnectionFactory();
		return new HttpRequester(httpConnectionFactory, getServiceUrl());
	}

	protected HttpConnectionFactory createHttpConnectionFactory() {
		return new DefaultHttpConnectionFactory();
	}

	protected Requester createComplexRequester() throws IOException {
		HttpClient httpClient = (HttpClient) this.httpClient;
		if (httpClient == null) {
			httpClient = createHttpClient();
		}
		return new CommonsHttpClientRequester(httpClient, getServiceUrl());
	}

	protected HttpClient createHttpClient() throws IOException {
		HttpClientBuilder builder = HttpClientBuilder.create();
		
		if (username != null) {
			builder.basicAuth(username, password);
		}
		builder.authPreemprive(authPreemptive);
		if (basicAuthHost != null) {
			builder.basicAuthHost(basicAuthHost);
		}
		if (basicAuthPort >= 0) {
			builder.basicAuthPort(basicAuthPort);
		}
		
		Assert.isTrue(trustKeyStoreResource == null || trustKeyStoreUrl == null,
				"trustKeyStoreResource and trustKeyStoreUrl cannot be defined at the same time");
		Assert.isTrue(keyStoreResource == null || keyStoreUrl == null,
				"keyStoreResource and keyStoreUrl cannot be defined at the same time");
		URL truststoreUrl = trustKeyStoreUrl;
		if (truststoreUrl == null && trustKeyStoreResource != null) {
			truststoreUrl = trustKeyStoreResource.getURL();
		}
		URL keystoreUrl = keyStoreUrl;
		if (keystoreUrl == null && keyStoreResource != null) {
			keystoreUrl = keyStoreResource.getURL();
		}
		boolean useSsl = truststoreUrl != null || keystoreUrl != null;
		
		if (useSsl) {
			Assert.notNull(trustKeyStorePassword, "trustKeyStorePassword cannot be null");
			Assert.notNull(keyStorePassword, "keyStorePassword cannot be null");
			Assert.notNull(sslHost, "sslHost cannot be null");
			
			builder.ssl(sslHost);
			builder.trustKeyStore(truststoreUrl, trustKeyStorePassword);
			builder.keyStore(keystoreUrl, keyStorePassword);
			builder.secure("https", sslPort);
		}
		HttpClient httpClient = builder.build();
		return httpClient;
	}

	protected Object createXRemotingProxy(XRemotingProxyFactory proxyFactory) {
		return proxyFactory.create(getServiceInterface(), getBeanClassLoader());
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (this.xremotingProxy == null) {
			throw new IllegalStateException("XRemotingClientInterceptor is not properly initialized - " +
					"invoke 'prepare' before attempting any operations");
		}

		try {
			return invocation.getMethod().invoke(this.xremotingProxy, invocation.getArguments());
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof XRemotingException) {
				XRemotingException xre = (XRemotingException) ex.getTargetException();
				throw convertXRemotingAccessException(xre);
			}
			else if (ex.getTargetException() instanceof UndeclaredThrowableException) {
				UndeclaredThrowableException utex = (UndeclaredThrowableException) ex.getTargetException();
				throw convertXRemotingAccessException(utex.getUndeclaredThrowable());
			}
			throw ex.getTargetException();
		}
		catch (Throwable ex) {
			throw new RemoteProxyFailureException(
					"Failed to invoke XRemoting proxy for remote service [" + getServiceUrl() + "]", ex);
		}
	}
	
	protected RemoteAccessException convertXRemotingAccessException(Throwable ex) {
		if (ex instanceof ClassNotFoundException) {
			throw new RemoteAccessException(
					"Cannot deserialize result from XRemoting remote service [" + getServiceUrl() + "]", ex);
		}
		else {
			throw new RemoteAccessException(
			    "Cannot access XRemoting remote service at [" + getServiceUrl() + "]", ex);
		}
	}
	
}
