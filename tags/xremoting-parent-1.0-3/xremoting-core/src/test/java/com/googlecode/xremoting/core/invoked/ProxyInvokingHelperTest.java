package com.googlecode.xremoting.core.invoked;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.xremoting.core.exception.InvokedSideInvocationException;
import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.message.Result;
import com.googlecode.xremoting.core.message.Thrown;
import com.googlecode.xremoting.core.spi.Serializer;
import com.googlecode.xremoting.core.test.CoolService;
import com.googlecode.xremoting.core.xstream.XStreamSerializer;

public class ProxyInvokingHelperTest {
	@Test
	public void testInvoke() throws Exception {
		Serializer serializer = new XStreamSerializer();
		Invoker invoker = new DefaultInvoker();
		
		Invocation invocation = new Invocation("doCoolStuff",
				new Class<?>[]{String.class, Integer.class, int.class, Class[].class},
				new Object[]{"abc", 14, -166, new Class[]{CoolService.class, Object.class}});
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(invocation, baos);
		byte[] bytes = baos.toByteArray();
		baos = new ByteArrayOutputStream();
		
		ProxyInvokingHelper helper = new ProxyInvokingHelper();
		helper.invoke(new CoolService(), new ByteArrayInputStream(bytes), baos, serializer, invoker,
				new InvocationRestriction(CoolService.class));
		
		bytes = baos.toByteArray();
		Result result = serializer.deserialize(new ByteArrayInputStream(bytes));
		Assert.assertEquals("abc14-166CoolService", result.getObject());
	}
	
	@Test
	public void testThrow() throws Exception {
		Serializer serializer = new XStreamSerializer();
		Invoker invoker = new DefaultInvoker();
		
		Invocation invocation = new Invocation("throwing",
				new Class<?>[]{}, new Object[]{});
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(invocation, baos);
		byte[] bytes = baos.toByteArray();
		baos = new ByteArrayOutputStream();
		
		ProxyInvokingHelper helper = new ProxyInvokingHelper();
		helper.invoke(new CoolService(), new ByteArrayInputStream(bytes), baos, serializer, invoker,
				new InvocationRestriction(CoolService.class));
		
		bytes = baos.toByteArray();
		Thrown thrown = serializer.deserialize(new ByteArrayInputStream(bytes));
		Assert.assertEquals(Exception.class, thrown.getThrowable().getClass());
		Assert.assertEquals("Just thrown", thrown.getThrowable().getMessage());
	}
	
	@Test
	public void testWrongMethod() throws Exception {
		Serializer serializer = new XStreamSerializer();
		Invoker invoker = new DefaultInvoker();
		
		Invocation invocation = new Invocation("noSuchMethod",
				new Class<?>[]{}, new Object[]{});
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(invocation, baos);
		byte[] bytes = baos.toByteArray();
		baos = new ByteArrayOutputStream();
		
		ProxyInvokingHelper helper = new ProxyInvokingHelper();
		helper.invoke(new CoolService(), new ByteArrayInputStream(bytes), baos, serializer, invoker,
				new InvocationRestriction(CoolService.class));
		
		bytes = baos.toByteArray();
		Thrown thrown = serializer.deserialize(new ByteArrayInputStream(bytes));
		Assert.assertEquals(InvokedSideInvocationException.class, thrown.getThrowable().getClass());
	}
}
