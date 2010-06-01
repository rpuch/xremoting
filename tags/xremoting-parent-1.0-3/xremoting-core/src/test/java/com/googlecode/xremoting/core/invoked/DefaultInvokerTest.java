package com.googlecode.xremoting.core.invoked;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.xremoting.core.exception.InvokedSideInvocationException;
import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.test.A;
import com.googlecode.xremoting.core.test.ABImpl;
import com.googlecode.xremoting.core.test.CoolService;

public class DefaultInvokerTest {
	
	private DefaultInvoker defaultInvoker = new DefaultInvoker();
	
	@Test
	public void testInvoke() throws Throwable {
		CoolService coolService = new CoolService();
		Invocation invocation = new Invocation("doCoolStuff",
				new Class<?>[]{String.class, Integer.class, int.class, Class[].class},
				new Object[]{"abc", 14, -166, new Class[]{CoolService.class, Object.class}});
		Object result = defaultInvoker.invoke(coolService, invocation, new InvocationRestriction(CoolService.class));
		Assert.assertEquals("abc14-166CoolService", result);
	}
	
	@Test
	public void testThrow() throws Throwable {
		CoolService coolService = new CoolService();
		Invocation invocation = new Invocation("throwing",
				new Class<?>[]{}, new Object[]{});
		try {
			defaultInvoker.invoke(coolService, invocation, new InvocationRestriction(CoolService.class));
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(Exception.class, e.getClass());
			Assert.assertEquals("Just thrown", e.getMessage());
		}
	}

	@Test
	public void testWrongMethod() throws Throwable {
		CoolService coolService = new CoolService();
		Invocation invocation = new Invocation("noSuchMethod",
				new Class<?>[]{}, new Object[]{});
		try {
			defaultInvoker.invoke(coolService, invocation, new InvocationRestriction(CoolService.class));
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(InvokedSideInvocationException.class, e.getClass());
		}
	}
	
	@Test
	public void testExposedMethod() throws Throwable {
		ABImpl ab = new ABImpl();
		Invocation invocation;
		invocation = new Invocation("a", new Class<?>[]{}, null);
		defaultInvoker.invoke(ab, invocation, new InvocationRestriction(A.class));
	}
	
	@Test
	public void testNotExposedMethodInInterface() throws Throwable {
		ABImpl ab = new ABImpl();
		Invocation invocation;
		invocation = new Invocation("b", new Class<?>[]{}, null);
		try {
			defaultInvoker.invoke(ab, invocation, new InvocationRestriction(A.class));
			Assert.fail();
		} catch (InvokedSideInvocationException e) {
			// expected
		}
	}
	
	@Test
	public void testNotExposedMethodInClass() throws Throwable {
		ABImpl ab = new ABImpl();
		Invocation invocation;
		invocation = new Invocation("c", new Class<?>[]{}, null);
		try {
			defaultInvoker.invoke(ab, invocation, new InvocationRestriction(A.class));
			Assert.fail();
		} catch (InvokedSideInvocationException e) {
			// expected
		}
	}
}
