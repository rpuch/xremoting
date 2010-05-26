package com.googlecode.xremoting.core.commonshttpclient;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
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
	
	private boolean authenticationPreemprive = true;
	
	private String basicAuthUsername;
	private String basicAuthPassword;
	private String basicAuthHost;
	private int basicAuthPort = -1;

	private Map<String, SslHostConfig> sslHostConfigs = new HashMap<String, SslHostConfig>();
	private String secureHost;
	private SslHostConfig sslHostConfig;
	
	public static HttpClientBuilder create() {
		return new HttpClientBuilder();
	}
	
	public HttpClientBuilder authPreemprive(boolean pre) {
		authenticationPreemprive = pre;
		return this;
	}
		
	public HttpClientBuilder basicAuth(String username, String password) {
		basicAuthUsername = username;
		basicAuthPassword = password;
		return this;
	}
	
	public HttpClientBuilder basicAuthHost(String host) {
		basicAuthHost = host;
		return this;
	}
	
	public HttpClientBuilder basicAuthPort(int port) {
		basicAuthPort = port;
		return this;
	}
	
	public HttpClientBuilder ssl(String host) {
		if (sslHostConfig != null) {
			sslHostConfigs.put(secureHost, sslHostConfig);
		}
		secureHost = host;
		sslHostConfig = new SslHostConfig();
		return this;
	}
	
	public HttpClientBuilder trustKeyStore(URL url, String password) {
		sslHostConfig.trustKeyStoreUrl = url;
		sslHostConfig.trustKeyStorePassword = password;
		return this;
	}
	
	public HttpClientBuilder keyStore(URL url, String password) {
		sslHostConfig.keyStoreUrl = url;
		sslHostConfig.keyStorePassword = password;
		return this;
	}
	
	public HttpClientBuilder secure(String schema, int port) {
		sslHostConfig.secureSchema = schema;
		sslHostConfig.securePort = port;
		return this;
	}
	
	public HttpClient build() {
		HttpClient httpClient = new HttpClient();

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
