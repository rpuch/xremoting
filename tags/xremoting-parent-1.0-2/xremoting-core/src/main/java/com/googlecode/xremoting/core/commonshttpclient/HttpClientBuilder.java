package com.googlecode.xremoting.core.commonshttpclient;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import com.googlecode.xremoting.core.commonshttpclient.ssl.AuthSSLProtocolSocketFactory;

/**
 * Used to build an HttpClient using method chain pattern.
 * 
 * @author Roman Puchkovskiy
 */
public class HttpClientBuilder {
	
	private String proxyHost;
	private int proxyPort = -1;
	
	private boolean authenticationPreemprive = true;
	
	private String basicAuthUsername;
	private String basicAuthPassword;
	private String basicAuthHost;
	private int basicAuthPort = -1;

	private Map<String, SslHostConfig> sslHostConfigs = new HashMap<String, SslHostConfig>();
	private String secureHost;
	private SslHostConfig sslHostConfig;
	
	/**
	 * Creates a new builder.
	 * 
	 * @return builder
	 */
	public static HttpClientBuilder create() {
		return new HttpClientBuilder();
	}
	
	/**
	 * Sets proxy host. If called, proxy will be used by the resulting
	 * HttpClient, else it will connect directly.
	 * 
	 * @param host	proxy host
	 * @return this
	 */
	public HttpClientBuilder proxyHost(String host) {
		proxyHost = host;
		return this;
	}
	
	/**
	 * Sets proxy port.
	 * 
	 * @param port	proxy port
	 * @return this
	 * @see #proxyHost(String)
	 */
	public HttpClientBuilder proxyPort(int port) {
		proxyPort = port;
		return this;
	}
	
	/**
	 * Sets whether the resulting HttpClient should send authorization before
	 * it's requested by the server. If not set, it's true.
	 * Ignored if Basic authentication is not configured.
	 * 
	 * @param pre	whether to use auth preemptive
	 * @return this
	 * @see #basicAuth(String, String)
	 */
	public HttpClientBuilder authPreemprive(boolean pre) {
		authenticationPreemprive = pre;
		return this;
	}

	/**
	 * Turns on a Basic authentication.
	 * 
	 * @param username	username to send
	 * @param password	password to send
	 * @return this
	 * @see #basicAuthHost(String)
	 * @see #basicAuthPort(int)
	 */
	public HttpClientBuilder basicAuth(String username, String password) {
		basicAuthUsername = username;
		basicAuthPassword = password;
		return this;
	}
	
	/**
	 * Constraints host for which Basic auth is sent. If not called, Basic
	 * auth will be sent to any host.
	 * Ignored if Basic auth is not configured.
	 * 
	 * @param host	host for which to send Basic auth (for others it will not
	 * be sent)
	 * @return this
	 * @see #basicAuth(String, String)
	 */
	public HttpClientBuilder basicAuthHost(String host) {
		basicAuthHost = host;
		return this;
	}
	
	/**
	 * Constraints port for which Basic auth is sent. If not called, Basic
	 * auth will be sent for connections to any port.
	 * Ignored if Basic auth is not configured.
	 * 
	 * @param port	port for which to send Basic auth (for others it will not
	 * be sent)
	 * @return this
	 * @see #basicAuth(String, String)
	 */
	public HttpClientBuilder basicAuthPort(int port) {
		basicAuthPort = port;
		return this;
	}
	
	/**
	 * Begins SSL configuration <em>for the given host</em>. Actual
	 * configuration must follow (see {@link #trustKeyStore(URL, String)},
	 * {@link #keyStore(URL, String)}, {@link #secure(String, int)}).
	 * Allows to enable both server validation (by supplying keyStore) and
	 * client authentication (by supplying trustKeyStore).
	 * 
	 * @param host	host for which to enable SSL
	 * @return this
	 * @see #trustKeyStore(URL, String)
	 * @see #keyStore(URL, String)
	 * @see #secureSchema(String)
	 * @see #securePort(int)
	 */
	public HttpClientBuilder ssl(String host) {
		if (sslHostConfig != null) {
			sslHostConfigs.put(secureHost, sslHostConfig);
		}
		secureHost = host;
		sslHostConfig = new SslHostConfig();
		return this;
	}

	/**
	 * Configures a trust store for the current SSL host (see
	 * {@link #ssl(String)}). If set, SSL client authentication will be used
	 * when connecting to that host (i.e. the client certificate will be sent).
	 * 
	 * @param url		URL from which to obtain trust store
	 * @param password	trust store password
	 * @return this
	 * @see #ssl(String)
	 * @see #keyStore(URL, String)
	 */
	public HttpClientBuilder trustKeyStore(URL url, String password) {
		if (sslHostConfig == null) {
			throw new IllegalStateException("ssl(String) must be called before this");
		}
		sslHostConfig.trustKeyStoreUrl = url;
		sslHostConfig.trustKeyStorePassword = password;
		return this;
	}
	
	/**
	 * Configures a key store for the current SSL host (see
	 * {@link #ssl(String)}). If set, SSL server validation will be used (i.e.
	 * the server certificate will be requested and validated).
	 * 
	 * @param url		URL from which to obtain key store
	 * @param password	key store password
	 * @return this
	 * @see #ssl(String)
	 * @see #trustKeyStore(URL, String)
	 */
	public HttpClientBuilder keyStore(URL url, String password) {
		if (sslHostConfig == null) {
			throw new IllegalStateException("ssl(String) must be called before this");
		}
		sslHostConfig.keyStoreUrl = url;
		sslHostConfig.keyStorePassword = password;
		return this;
	}
	
	/**
	 * Configures secure schema for the HTTPS for current host
	 * (see {@link #ssl(String)}).
	 * 
	 * @param schema	secure schema (usually, this is "https")
	 * @return this
	 * @see #ssl(String)
	 */
	public HttpClientBuilder secureSchema(String schema) {
		if (sslHostConfig == null) {
			throw new IllegalStateException("ssl(String) must be called before this");
		}
		sslHostConfig.secureSchema = schema;
		return this;
	}
	
	/**
	 * Configures secure port for the HTTPS for current host
	 * (see {@link #ssl(String)}).
	 * 
	 * @param port		secure port (if this method is not called, it's
	 * defaulted to 443)
	 * @return this
	 * @see #ssl(String)
	 */
	public HttpClientBuilder securePort(int port) {
		if (sslHostConfig == null) {
			throw new IllegalStateException("ssl(String) must be called before this");
		}
		sslHostConfig.securePort = port;
		return this;
	}

	/**
	 * Builds the configured HttpClient.
	 * 
	 * @return HttpClient instance
	 */
	public HttpClient build() {
		HttpClient httpClient = new HttpClient();
		
		if (proxyHost != null) {
			ProxyHost h;
			if (proxyPort == -1) {
				h = new ProxyHost(proxyHost);
			} else {
				h = new ProxyHost(proxyHost, proxyPort);
			}
			httpClient.getHostConfiguration().setProxyHost(h);
		}

		httpClient.getParams().setAuthenticationPreemptive(authenticationPreemprive);
		
		if (basicAuthUsername != null) {
			AuthScope authscope = new AuthScope(
					basicAuthHost == null ? AuthScope.ANY_HOST : basicAuthHost,
					basicAuthPort == -1 ? AuthScope.ANY_PORT : basicAuthPort);
			Credentials credentials = new UsernamePasswordCredentials(basicAuthUsername, basicAuthPassword);
			httpClient.getState().setCredentials(authscope, credentials);
		}
		
		if (sslHostConfig != null) {
			sslHostConfigs.put(secureHost, sslHostConfig);
		}
		
		for (Entry<String, SslHostConfig> entry : sslHostConfigs.entrySet()) {
			String host = entry.getKey();
			SslHostConfig config = entry.getValue();
			ProtocolSocketFactory factory = new AuthSSLProtocolSocketFactory(
					config.keyStoreUrl, config.keyStorePassword,
					config.trustKeyStoreUrl, config.trustKeyStorePassword);
			Protocol protocol = createProtocol(factory, config);
			httpClient.getHostConfiguration().setHost(host, config.securePort, protocol);
		}
		
		return httpClient;
	}
	
	private Protocol createProtocol(ProtocolSocketFactory factory, SslHostConfig config) {
		return new Protocol(config.secureSchema, factory, config.securePort);
	}
	
	private static class SslHostConfig {
		public URL trustKeyStoreUrl;
		public URL keyStoreUrl;
		public String trustKeyStorePassword;
		public String keyStorePassword;
		public String secureSchema = "https";
		public int securePort = 443;
	}
}
