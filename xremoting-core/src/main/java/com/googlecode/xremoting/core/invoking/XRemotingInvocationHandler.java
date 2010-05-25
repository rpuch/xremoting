package com.googlecode.xremoting.core.invoking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.googlecode.xremoting.core.exception.InvokingSideInvocationException;
import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.message.Result;
import com.googlecode.xremoting.core.message.Thrown;
import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.spi.SerializationException;
import com.googlecode.xremoting.core.spi.Serializer;

public class XRemotingInvocationHandler implements InvocationHandler {
	
	private Serializer serializer;
	private Requester requester;

	public XRemotingInvocationHandler(Serializer serializer, Requester requester) {
		super();
		this.serializer = serializer;
		this.requester = requester;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Invocation invocation = new Invocation(method.getName(), method.getParameterTypes(), args);
		Request request = null;
		try {
			request = requester.createRequest();
			OutputStream os = request.getOutputStream();
			serializer.serialize(invocation, os);
			request.commitRequest();
			InputStream is = request.getInputStream();
			Object result = serializer.deserialize(is);
			if (result instanceof Result) {
				Result returnValue = (Result) result;
				return returnValue.getObject();
			} else if (result instanceof Thrown) {
				Thrown thrown = (Thrown) result;
				throw thrown.getThrowable();
			} else {
				throw new InvokingSideInvocationException("Instance of unexpected class returned; Result or Thrown expected but got " + result.getClass().getName());
			}
		} catch (IOException e) {
			throw new InvokingSideInvocationException(e);
		} catch (SerializationException e) {
			throw new InvokingSideInvocationException(e);
		} finally {
			if (request != null) {
				request.releaseRequest();
			}
		}
	}
	
}