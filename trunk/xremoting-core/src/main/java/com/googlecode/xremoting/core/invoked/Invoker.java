package com.googlecode.xremoting.core.invoked;

import com.googlecode.xremoting.core.exception.InvokedSideInvocationException;
import com.googlecode.xremoting.core.message.Invocation;

/**
 * Services a remote invocation locally.
 * 
 * @author Roman Puchkovskiy
 */
public interface Invoker {
	/**
	 * Invokes an invocation on a target object with restriction.
	 * 
	 * @param target		object on which to call method
	 * @param invocation	what to invoke, and parameters
	 * @param restriction	attributes which define how invocation will be
	 * restricted
	 * @return invocation result
	 * @throws InvokedSideInvocationException	thrown if something is wrong
	 * with invocation itself, like NoSuchMethodException or alike. Also,
	 * thrown if invocation is not allowed due to restriction. 
	 * @throws Throwable						anything thrown by the target
	 */
	Object invoke(Object target, Invocation invocation,
			InvocationRestriction restriction)
			throws InvokedSideInvocationException, Throwable;
}
