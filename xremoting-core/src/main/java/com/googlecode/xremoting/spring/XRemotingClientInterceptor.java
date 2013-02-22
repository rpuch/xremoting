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
	
	private String proxyHost;
	private int proxyPort = -1;
	
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
    private String[] sslEnabledProtocols = null;

    private int connectionTimeout = 10000;
    private int soTimeout = 10000;
    private long connectionManagerTimeout = 10000;
	
	private Object xremotingProxy;
	
	/**
	 * Sets classloader to use when constructing a Proxy.
	 * 
	 * @param beanClassLoader	classloader
	 */
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}
	
	/**
	 * Sets a {@link Requester} to use. If this is set, requester will not
	 * be built but this one will be used.
	 * 
	 * @param requester	requester
	 */
	public void setRequester(Requester requester) {
		this.requester = requester;
	}

	/**
	 * Sets an HttpClient to use with {@link CommonsHttpClientRequester}. If
	 * {@link #setRequester(Requester)} is not called, then
	 * CommonsHttpClientRequester will use the supplied HttpClient instead of
	 * building it from configuration like proxy config, SSL config etc.
	 * 
	 * @param httpClient	HTTPClient to set
	 */
	public void setHttpClient(Object httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * Sets a proxy host to be used by a built HttpClient. Only used
	 * if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param proxyHost	host
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * Sets a proxy port to be used by a built HttpClient. Only used
	 * if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param proxyPort	port
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * Sets a username to be used by a built HttpClient (for Basic auth).
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param username	name
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Sets a password to be used by a built HttpClient (for Basic auth).
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param password	password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets whether to use authPreemptive by a built HttpClient (for Basic auth).
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param authPreemptive	value
	 */
	public void setAuthPreemptive(boolean authPreemptive) {
		this.authPreemptive = authPreemptive;
	}

	/**
	 * Sets a host for which Basic auth will be used by a built HttpClient.
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param basicAuthHost	host
	 */
	public void setBasicAuthHost(String basicAuthHost) {
		this.basicAuthHost = basicAuthHost;
	}

	/**
	 * Sets a port for which Basic auth will be used by a built HttpClient.
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param basicAuthPort	port
	 */
	public void setBasicAuthPort(int basicAuthPort) {
		this.basicAuthPort = basicAuthPort;
	}

	/**
	 * Sets a trust store URL to be used by a built HttpClient. If this is
	 * set, server SSL certificate will be requested and validated.
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param trustKeyStoreUrl	URL
	 */
	public void setTrustKeyStoreUrl(URL trustKeyStoreUrl) {
		this.trustKeyStoreUrl = trustKeyStoreUrl;
	}

	/**
	 * Sets a trust store resource to be used by a built HttpClient. If this is
	 * set, server SSL certificate will be requested and validated.
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param trustKeyStoreResource	resource
	 */
	public void setTrustKeyStoreResource(Resource trustKeyStoreResource) {
		this.trustKeyStoreResource = trustKeyStoreResource;
	}

	/**
	 * Sets a key store URL to be used by a built HttpClient. If this is
	 * set, client will send its certificate to the server.
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param keyStoreUrl	URL
	 */
	public void setKeyStoreUrl(URL keyStoreUrl) {
		this.keyStoreUrl = keyStoreUrl;
	}

	/**
	 * Sets a key store resource to be used by a built HttpClient. If this is
	 * set, client will send its certificate to the server.
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param keyStoreResource	resource
	 */
	public void setKeyStoreResource(Resource keyStoreResource) {
		this.keyStoreResource = keyStoreResource;
	}

	/**
	 * Sets a trust store password to be used by a built HttpClient.
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param trustKeyStorePassword	password
	 */
	public void setTrustKeyStorePassword(String trustKeyStorePassword) {
		this.trustKeyStorePassword = trustKeyStorePassword;
	}

	/**
	 * Sets a key store password to be used by a built HttpClient.
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param keyStorePassword	password
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	/**
	 * Sets SSL host to be used by a built HttpClient. This must match a host
	 * to which requests will be made (i.e. {@link #getServiceUrl()}).
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param sslHost	host
	 */
	public void setSslHost(String sslHost) {
		this.sslHost = sslHost;
	}

	/**
	 * Sets SSL port to be used by a built HttpClient. This must match a port
	 * to which requests will be made (i.e. {@link #getServiceUrl()}).
	 * Only used if HttpClient is built (see {@link #setRequester(Requester)},
	 * {@link #setHttpClient(Object)}.
	 * 
	 * @param sslPort	port
	 */
	public void setSslPort(int sslPort) {
		this.sslPort = sslPort;
	}

    /**
     * Sets a list of enabled SSL protocols. No other protocol
     * will not be used.
     *
     * @param sslEnabledProtocols   protocols
     */
    public void setSslEnabledProtocols(String[] sslEnabledProtocols) {
        this.sslEnabledProtocols = sslEnabledProtocols;
    }

    /**
     * Sets connection timeout (in millis).
     *
     * @param connectionTimeout connection timeout (millis)
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Sets SO timeout (in millis).
     *
     * @param soTimeout SO timeout (millis)
     */
    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    /**
     * Sets maximum time to wait for connection to be obtained from
     * a connection manager (in millis).
     *
     * @param connectionManagerTimeout  connection manager timeout (millis)
     */
    public void setConnectionManagerTimeout(long connectionManagerTimeout) {
        this.connectionManagerTimeout = connectionManagerTimeout;
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

	/**
	 * Initializes the interceptor. Must be called prior to making invocations.
	 * 
	 * @throws IOException	thrown if truststore or keystore could not be
	 * obtained
	 */
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
		HttpClientBuilder builder = createAndConfigureHttpClientBuilder();
        return builder.build();
	}

	protected HttpClientBuilder createAndConfigureHttpClientBuilder()
			throws IOException {
		HttpClientBuilder builder = HttpClientBuilder.create();
		
		if (proxyHost != null) {
			builder.proxyHost(proxyHost);
			if (proxyPort >= 0) {
				builder.proxyPort(proxyPort);
			}
		}
		
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
			Assert.isTrue(truststoreUrl == null || trustKeyStorePassword != null,
					"trustKeyStorePassword cannot be null");
			Assert.isTrue(keystoreUrl == null || keyStorePassword != null,
					"keyStorePassword cannot be null");
			Assert.notNull(sslHost, "sslHost cannot be null");
			
			builder.ssl(sslHost);
			builder.trustKeyStore(truststoreUrl, trustKeyStorePassword);
			builder.keyStore(keystoreUrl, keyStorePassword);
			builder.securePort(sslPort);
            builder.sslEnabledProtocols(sslEnabledProtocols);
		}

        builder.timeouts(connectionTimeout, soTimeout, connectionManagerTimeout);

		return builder;
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
