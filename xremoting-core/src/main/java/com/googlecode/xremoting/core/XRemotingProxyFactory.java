package com.googlecode.xremoting.core;

import java.lang.reflect.Proxy;

import org.apache.commons.httpclient.HttpClient;

import com.googlecode.xremoting.core.commonshttpclient.CommonsHttpClientRequester;
import com.googlecode.xremoting.core.exception.InitializationException;
import com.googlecode.xremoting.core.http.HttpRequester;
import com.googlecode.xremoting.core.invoking.XRemotingInvocationHandler;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.spi.Serializer;
import com.googlecode.xremoting.core.xstream.XStreamSerializer;

/**
 * <p>
 * Factory for proxy instances through which remote calls may be made.
 * </p>
 * <p>
 * Here's an example:
 * TODO
 * </p>
 * <p>
 * There're basically two things you can parameterize: {@link Requester} and
 * {@link Serializer}. For a Serializer, XStream-based serialization is the
 * default. For Requester, the simplest implementation is {@link HttpRequester}
 * which allows to invoke service exposed via HTTP.
 * </p>
 * <p>
 * If you need some of more sophisticated features of HTTP (like SSL or
 * authentication), you may consider {@link CommonsHttpClientRequester} which
 * gives access to the full power of {@link HttpClient}.
 * </p>
 * 
 * @author Roman Puchkovskiy
 * @see Serializer
 * @see Requester
 * @see XStreamSerializer
 * @see HttpRequester
 * @see CommonsHttpClientRequester
 */
public class XRemotingProxyFactory {
	
	private Serializer serializer;
	private Requester requester;

	/**
	 * Creates a factory with XStream-based serialization.
	 * 
	 * @param requester		requester to use
	 */
	public XRemotingProxyFactory(Requester requester) {
		this(requester, new XStreamSerializer());
	}
	
	/**
	 * Creates a factory with serialization implemented by class given by name.
	 * Default classloader will be used to load that class. Default classloader
	 * is determined like this:
	 * <ol>
	 * <li>if context classloader is defined, it's used</li>
	 * <li>else classloader which loaded this class is used</li>
	 * </ol>
	 * 
	 * @param requester				requester to use
	 * @param serializerClassName	name of the {@link Serializer}
	 * implementation class
	 */
	public XRemotingProxyFactory(Requester requester, String serializerClassName) {
		init(requester, serializerClassName, getDefaultClassLoader());
	}
	
	/**
	 * Creates a factory with serialization implemented by class given by name,
	 * and loaded from the given classloader.
	 * 
	 * @param requester				requester to use
	 * @param serializerClassName	name of the {@link Serializer}
	 * implementation class
	 * @param spiClassLoader		classloader from which to load Serializer
	 * implementation
	 */
	public XRemotingProxyFactory(Requester requester, String serializerClassName,
			ClassLoader spiClassLoader) {
		init(requester, serializerClassName, spiClassLoader);
	}
	
	/**
	 * Creates a factory with the given {@link Serializer} implementation class.
	 * 
	 * @param requester			requester to use
	 * @param serializerClass	class of a {@link Serializer} implementation
	 */
	public XRemotingProxyFactory(Requester requester, Class<? extends Serializer> serializerClass) {
		init(requester, serializerClass);
	}
	
	/**
	 * Creates a factory with the given {@link Serializer}.
	 * 
	 * @param requester		requester to use
	 * @param serializer	serializer to use
	 */
	public XRemotingProxyFactory(Requester requester, Serializer serializer) {
		init(requester, serializer);
	}
	
	@SuppressWarnings("unchecked")
	private void init(Requester requester, String serializerClassName,
			ClassLoader spiClassLoader) {
		Class<? extends Serializer> clazz;
		try {
			clazz = (Class<? extends Serializer>) Class.forName(serializerClassName, true, spiClassLoader);
		} catch (ClassNotFoundException e) {
			throw new InitializationException(e);
		}
		init(requester, clazz);
	}
	
	private void init(Requester requester, Class<? extends Serializer> serializerClass) {
		init(requester, createSerializer(serializerClass));
	}
	
	private void init(Requester requester, Serializer serializer) {
		this.requester = requester;
		this.serializer = serializer;
	}
	
	/**
	 * Creates a proxy for the given interface.
	 * Default classloader will be used for proxy. Default classloader
	 * is determined like this:
	 * <ol>
	 * <li>if context classloader is defined, it's used</li>
	 * <li>else classloader which loaded this class is used</li>
	 * </ol>
	 * 
	 * @param iface		interface for which to create proxy
	 * @return proxy
	 */
	public Object create(Class<?> iface) {
		return create(iface, getDefaultClassLoader());
	}
	
	/**
	 * Creates a proxy for the given interfaces.
	 * Default classloader will be used for proxy. Default classloader
	 * is determined like this:
	 * <ol>
	 * <li>if context classloader is defined, it's used</li>
	 * <li>else classloader which loaded this class is used</li>
	 * </ol>
	 * 
	 * @param ifaces	interfaces for which to create proxy
	 * @return proxy
	 */
	public Object create(Class<?>[] ifaces) {
		return create(ifaces, getDefaultClassLoader());
	}
	
	/**
	 * Creates a proxy for the given interface.
	 * 
	 * @param iface			interface for which to create proxy
	 * @param proxyLoader	classloader for proxy
	 * @return proxy
	 */
	public Object create(Class<?> iface, ClassLoader proxyLoader) {
		return create(new Class[]{iface}, proxyLoader);
	}
	
	/**
	 * Creates a proxy for the given interfaces.
	 * 
	 * @param ifaces		interfaces for which to create proxy
	 * @param proxyLoader	classloader for proxy
	 * @return proxy
	 */
	public Object create(Class<?>[] ifaces, ClassLoader proxyLoader) {
		return Proxy.newProxyInstance(proxyLoader, ifaces,
				new XRemotingInvocationHandler(serializer, requester));
	}

	protected Serializer createSerializer(
			Class<? extends Serializer> serializerClass) {
		try {
			return serializerClass.newInstance();
		} catch (InstantiationException e) {
			throw new InitializationException(e);
		} catch (IllegalAccessException e) {
			throw new InitializationException(e);
		}
	}
	
	protected ClassLoader getDefaultClassLoader() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = getClass().getClassLoader();
		}
		return classLoader;
	}
}
