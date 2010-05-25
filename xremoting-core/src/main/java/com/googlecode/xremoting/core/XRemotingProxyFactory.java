package com.googlecode.xremoting.core;

import java.lang.reflect.Proxy;

import com.googlecode.xremoting.core.exception.InitializationException;
import com.googlecode.xremoting.core.invoking.XRemotingInvocationHandler;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.spi.Serializer;

public class XRemotingProxyFactory {
	
	private Serializer serializer;
	private Requester requester;
		
	public XRemotingProxyFactory(Requester requester) {
		this(requester, "com.googlecode.xremoting.core.xstream.XStreamSerializer");
	}
	
	public XRemotingProxyFactory(Requester requester, String serializerClassName) {
		this(requester, serializerClassName, XRemotingProxyFactory.class.getClassLoader());
	}
	
	@SuppressWarnings("unchecked")
	public XRemotingProxyFactory(Requester requester, String serializerClassName, ClassLoader spiClassLoader) {
		Class<? extends Serializer> clazz;
		try {
			clazz = (Class<? extends Serializer>) Class.forName(serializerClassName, true, spiClassLoader);
		} catch (ClassNotFoundException e) {
			throw new InitializationException(e);
		}
		init(requester, clazz);
	}
	
	public XRemotingProxyFactory(Requester requester, Class<? extends Serializer> serializerClass) {
		init(requester, serializerClass);
	}
	
	public XRemotingProxyFactory(Requester requester, Serializer serializer) {
		init(requester, serializer);
	}
	
	private void init(Requester requester, Class<? extends Serializer> serializerClass) {
		init(requester, createSerializer(serializerClass));
	}
	
	private void init(Requester requester, Serializer serializer) {
		this.requester = requester;
		this.serializer = serializer;
	}
	
	public Object create(Class<?> iface) {
		return create(iface, getDefaultClassLoader());
	}
	
	public Object create(Class<?>[] ifaces) {
		return create(ifaces, getDefaultClassLoader());
	}
	
	public Object create(Class<?> iface, ClassLoader proxyLoader) {
		return create(new Class[]{iface}, proxyLoader);
	}
	
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
