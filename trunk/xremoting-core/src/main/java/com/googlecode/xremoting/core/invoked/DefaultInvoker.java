package com.googlecode.xremoting.core.invoked;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.googlecode.xremoting.core.exception.InvokedSideInvocationException;
import com.googlecode.xremoting.core.message.Invocation;

/**
 * Default {@link Invoker} implementation. Only allows to invoke methods which
 * are defined by interfaces specified in the {@link InvocationRestriction}.
 * 
 * @author Roman Puchkovskiy
 */
public class DefaultInvoker implements Invoker {
	
	public Object invoke(Object target, Invocation invocation,
			InvocationRestriction restriction)
			throws InvokedSideInvocationException, Throwable {
		try {
			Method method = target.getClass().getMethod(invocation.getMethodName(), invocation.getArgTypes());
			checkMethod(method, restriction);
			return method.invoke(target, invocation.getArgs());
		} catch (SecurityException e) {
			throw new InvokedSideInvocationException(e);
		} catch (IllegalArgumentException e) {
			throw new InvokedSideInvocationException(e);
		} catch (NoSuchMethodException e) {
			throw new InvokedSideInvocationException(e);
		} catch (IllegalAccessException e) {
			throw new InvokedSideInvocationException(e);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	protected void checkMethod(Method method, InvocationRestriction restriction)
			throws InvokedSideInvocationException, SecurityException {
		boolean allowed = false;
		for (Class<?> iface : restriction.getInterfaces()) {
			try {
				iface.getMethod(method.getName(), method.getParameterTypes());
				allowed = true;
				break;
			} catch (NoSuchMethodException e) {
				// ignore
			}
		}
		if (!allowed) {
			throw new InvokedSideInvocationException("The given method (" + method + ") is not defined by any of exposed interfaces");
		}
	}

}
