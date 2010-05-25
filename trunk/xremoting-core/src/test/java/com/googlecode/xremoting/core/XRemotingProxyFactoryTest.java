package com.googlecode.xremoting.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.message.Result;
import com.googlecode.xremoting.core.message.Thrown;
import com.googlecode.xremoting.core.spi.SerializationException;
import com.googlecode.xremoting.core.spi.Serializer;
import com.googlecode.xremoting.core.test.A;
import com.googlecode.xremoting.core.test.B;
import com.googlecode.xremoting.core.test.CoolServiceInterface;
import com.googlecode.xremoting.core.test.DummyRequester;
import com.googlecode.xremoting.core.xstream.XStreamSerializer;

public class XRemotingProxyFactoryTest {
	@Test
	public void testInvoke() throws Exception {
		DummyRequester requester = new DummyRequester();
		XRemotingProxyFactory proxyFactory = new XRemotingProxyFactory(requester, new Serializer() {
			
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
				Assert.assertEquals(Invocation.class, object.getClass());
				Invocation invocation = (Invocation) object;
				Assert.assertEquals("doCoolStuff", invocation.getMethodName());
				Assert.assertArrayEquals(new Class<?>[]{String.class, Integer.class, int.class, Class[].class}, invocation.getArgTypes());
				Assert.assertArrayEquals(new Object[]{"abc", 10, -166, new Class<?>[]{String.class, Object.class}}, invocation.getArgs());
			}
			
			@SuppressWarnings("unchecked")
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				return (T) new Result("def");
			}
		});
		
		CoolServiceInterface service = (CoolServiceInterface) proxyFactory.create(CoolServiceInterface.class);
		String result = service.doCoolStuff("abc", 10, -166, new Class<?>[]{String.class, Object.class});
		Assert.assertEquals("def", result);
		
		requester.assertRequestsAreReleased();
	}
	
	@Test
	public void testThrow() throws Exception {
		DummyRequester requester = new DummyRequester();
		XRemotingProxyFactory proxyFactory = new XRemotingProxyFactory(requester, new Serializer() {
			
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
				Assert.assertEquals(Invocation.class, object.getClass());
				Invocation invocation = (Invocation) object;
				Assert.assertEquals("throwing", invocation.getMethodName());
				Assert.assertArrayEquals(new Class<?>[]{}, invocation.getArgTypes());
				Assert.assertArrayEquals(null, invocation.getArgs());
			}
			
			@SuppressWarnings("unchecked")
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				return (T) new Thrown(new Exception("catch me"));
			}
		});
		
		CoolServiceInterface service = (CoolServiceInterface) proxyFactory.create(CoolServiceInterface.class);
		try {
			service.throwing();
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(Exception.class, e.getClass());
			Assert.assertEquals("catch me", e.getMessage());
		}
		
		requester.assertRequestsAreReleased();
	}
	
	@Test
	public void testMultipleInterfaces() throws Exception {
		DummyRequester requester = new DummyRequester();
		XRemotingProxyFactory proxyFactory = new XRemotingProxyFactory(requester, new Serializer() {
			
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
			}
			
			@SuppressWarnings("unchecked")
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				return (T) new Result(null);
			}
		});
		
		Object proxy = proxyFactory.create(new Class<?>[]{A.class, B.class});
		A a = (A) proxy;
		B b = (B) proxy;
		a.a();
		b.b();
		
		requester.assertRequestsAreReleased();
	}
	
	@Test
	public void testInstantiation() throws Exception {
		DummyRequester requester = new DummyRequester();
		new XRemotingProxyFactory(requester);
		new XRemotingProxyFactory(requester, XStreamSerializer.class);
		new XRemotingProxyFactory(requester, new XStreamSerializer());
		new XRemotingProxyFactory(requester, XStreamSerializer.class.getName());
		new XRemotingProxyFactory(requester, XStreamSerializer.class.getName(),
				getClass().getClassLoader());
	}
}
