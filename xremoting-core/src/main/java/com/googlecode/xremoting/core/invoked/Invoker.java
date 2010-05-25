package com.googlecode.xremoting.core.invoked;

import com.googlecode.xremoting.core.exception.InvokedSideInvocationException;
import com.googlecode.xremoting.core.message.Invocation;

public interface Invoker {
	Object invoke(Object target, Invocation invocation,
			InvocationRestriction restriction)
			throws InvokedSideInvocationException, Throwable;
}
