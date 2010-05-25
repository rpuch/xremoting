package com.googlecode.xremoting.core.invoked;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.xremoting.core.exception.InvokedSideInvocationException;
import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.message.Result;
import com.googlecode.xremoting.core.message.Thrown;
import com.googlecode.xremoting.core.spi.SerializationException;
import com.googlecode.xremoting.core.spi.Serializer;

/**
 * Helper class for invoking on server side.
 * 
 * @author Roman Puchkovskiy
 */
public class ProxyInvokingHelper {
	public void invoke(Object target, InputStream is, OutputStream os,
			Serializer serializer, Invoker invoker,
			InvocationRestriction restriction) throws IOException {
		try {
			Object input = serializer.deserialize(is);
			if (input instanceof Invocation) {
				Invocation invocation = (Invocation) input;
				try {
					Object result = invoke(target, invocation, invoker, restriction);
					sendResult(os, result, serializer);
				} catch (Throwable e) {
					sendThrownThrowingOnSerializationError(os, e, serializer);
				}
			} else {
				sendThrownThrowingOnSerializationError(os,
						new InvokedSideInvocationException("Instance of Invocation was expected, but got " + input.getClass().getName()), serializer);
			}
		} catch (SerializationException e) {
			sendThrownThrowingOnSerializationError(os, e, serializer);
		}
	}
	
	protected Object invoke(Object target, Invocation invocation, Invoker invoker,
			InvocationRestriction restriction)
			throws InvokedSideInvocationException, Throwable {
		return invoker.invoke(target, invocation, restriction);
	}
	
	protected void sendResult(OutputStream os, Object result, Serializer serializer) throws SerializationException, IOException {
		send(os, new Result(result), serializer);
	}
	
	protected void sendThrown(OutputStream os, Throwable e, Serializer serializer) throws SerializationException, IOException {
		send(os, new Thrown(e), serializer);
	}
	
	protected void send(OutputStream os, Object object, Serializer serializer) throws SerializationException, IOException {
		serializer.serialize(object, os);
	}
	
	protected void sendThrownThrowingOnSerializationError(OutputStream os, Throwable t, Serializer serializer) throws IOException {
		try {
			sendThrown(os, t, serializer);
		} catch (SerializationException e) {
			throw new RuntimeException("Cannot serialize result/thrown", e);
		}
	}

}
