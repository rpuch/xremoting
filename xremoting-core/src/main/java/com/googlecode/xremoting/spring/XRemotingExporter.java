package com.googlecode.xremoting.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;

import com.googlecode.xremoting.core.invoked.DefaultInvoker;
import com.googlecode.xremoting.core.invoked.InvocationRestriction;
import com.googlecode.xremoting.core.invoked.Invoker;
import com.googlecode.xremoting.core.invoked.ProxyInvokingHelper;
import com.googlecode.xremoting.core.spi.Serializer;
import com.googlecode.xremoting.core.utils.ClassLoaderUtils;
import com.googlecode.xremoting.core.xstream.XStreamSerializer;

/**
 * Exposes XRemoting-based service using some protocol.
 * 
 * @author Roman Puchkovskiy
 */
public class XRemotingExporter extends RemoteExporter implements
		InitializingBean {
	
	protected Serializer serializer;
	protected Class<? extends Serializer> serializerClass;
	protected String serializerClassName;
	protected ClassLoader serializerClassLoader;
	protected Invoker invoker;
	
	protected ProxyInvokingHelper invokingHelper;
	protected InvocationRestriction restriction;

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public void setSerializerClass(Class<? extends Serializer> serializerClass) {
		this.serializerClass = serializerClass;
	}

	public void setSerializerClassName(String serializerClassName) {
		this.serializerClassName = serializerClassName;
	}

	public void setSerializerClassLoader(ClassLoader serializerClassLoader) {
		this.serializerClassLoader = serializerClassLoader;
	}

	public void setInvoker(Invoker invoker) {
		this.invoker = invoker;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(getServiceInterface(), "Service interface cannot be null");
		Assert.notNull(getService(), "Service interface cannot be null");
		int num = 0;
		if (serializer != null) {
			num++;
		}
		if (serializerClass != null) {
			num++;
		}
		if (serializerClassName != null) {
			num++;
		}
		Assert.isTrue(num <= 1, "Maximum one of serializer, serializerClass and serializerClassName may be specified");
		
		if (serializer == null) {
			serializer = createSerializer();
		}
		if (invoker == null) {
			invoker = createDefaultInvoker();
		}
		invokingHelper = createInvokingHelper();
		restriction = createRestriction();
	}

	protected Serializer createSerializer() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (serializerClass != null) {
			return serializerClass.newInstance();
		}
		if (serializerClassName != null) {
			ClassLoader classLoader = serializerClassLoader;
			if (classLoader == null) {
				classLoader = getDefaultSerializerClassLoader();
			}
			return (Serializer) Class.forName(serializerClassName, true, classLoader).newInstance();
		}
		// everything is null, so just create a default serializer
		return createDefaultSerializer();
	}

	protected Serializer createDefaultSerializer() {
		return new XStreamSerializer();
	}

	protected ClassLoader getDefaultSerializerClassLoader() {
		return ClassLoaderUtils.getDefaultClassLoader(getClass());
	}

	protected Invoker createDefaultInvoker() {
		return new DefaultInvoker();
	}
	
	protected ProxyInvokingHelper createInvokingHelper() {
		return new ProxyInvokingHelper();
	}
	
	protected InvocationRestriction createRestriction() {
		return new InvocationRestriction(getServiceInterface());
	}
	
	protected void invoke(InputStream is, OutputStream os) throws IOException {
		invokingHelper.invoke(getService(), is, os, serializer, invoker, restriction);
	}
}
