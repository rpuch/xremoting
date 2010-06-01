package com.googlecode.xremoting.core.invoking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.xremoting.core.exception.InvokingSideInvocationException;
import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.message.Result;
import com.googlecode.xremoting.core.message.Thrown;
import com.googlecode.xremoting.core.spi.SerializationException;
import com.googlecode.xremoting.core.spi.Serializer;
import com.googlecode.xremoting.core.test.CoolService;
import com.googlecode.xremoting.core.test.DummyRequester;
import com.googlecode.xremoting.core.test.Person;

public class XRemotingInvocationHandlerTest {
	@Test
	public void testInvoke() throws Throwable {
		DummyRequester requester = new DummyRequester();
		XRemotingInvocationHandler handler = new XRemotingInvocationHandler(new Serializer() {
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
				Assert.assertTrue(object instanceof Invocation);
				Invocation invocation = (Invocation) object;
				Assert.assertEquals("justInvoke", invocation.getMethodName());
				Assert.assertArrayEquals(new Class<?>[]{String.class, Object[].class}, invocation.getArgTypes());
				Assert.assertArrayEquals(new Object[]{"abc", new Object[]{1, true}}, invocation.getArgs());
			}
			
			@SuppressWarnings("unchecked")
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				Person person = new Person();
				person.setFirstName("Kashey");
				person.setAge(999);
				return (T) new Result(person);
			}
		}, requester);
		
		CoolService target = new CoolService();
		Method method = target.getClass().getMethod("justInvoke", new Class<?>[]{String.class, Object[].class});
		Person person = (Person) handler.invoke(target, method, new Object[]{"abc", new Object[]{1, true}});
		Assert.assertEquals("Kashey", person.getFirstName());
		Assert.assertEquals(999, person.getAge());
		
		requester.assertRequestsAreReleased();
	}
	
	@Test
	public void testThrow() throws Throwable {
		DummyRequester requester = new DummyRequester();
		XRemotingInvocationHandler handler = new XRemotingInvocationHandler(new Serializer() {
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
				Assert.assertTrue(object instanceof Invocation);
				Invocation invocation = (Invocation) object;
				Assert.assertEquals("throwing", invocation.getMethodName());
				Assert.assertArrayEquals(new Class<?>[]{}, invocation.getArgTypes());
				Assert.assertArrayEquals(new Object[]{}, invocation.getArgs());
			}
			
			@SuppressWarnings("unchecked")
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				return (T) new Thrown(new Exception("catch me!"));
			}
		}, requester);
		
		CoolService target = new CoolService();
		Method method = target.getClass().getMethod("throwing", new Class<?>[]{});
		try {
			handler.invoke(target, method, new Object[]{});
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(Exception.class, e.getClass());
			Assert.assertEquals("catch me!", e.getMessage());
		}
		
		requester.assertRequestsAreReleased();
	}
	
	@Test
	public void testUnexpectedClassReturned() throws Throwable {
		DummyRequester requester = new DummyRequester();
		XRemotingInvocationHandler handler = new XRemotingInvocationHandler(new Serializer() {
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
			}
			
			@SuppressWarnings("unchecked")
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				return (T) new Object();
			}
		}, requester);
		
		CoolService target = new CoolService();
		Method method = target.getClass().getMethod("throwing", new Class<?>[]{});
		try {
			handler.invoke(target, method, new Object[]{});
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(InvokingSideInvocationException.class, e.getClass());
		}
		
		requester.assertRequestsAreReleased();
	}
	
	@Test
	public void testIOExceptionInSerialize() throws Throwable {
		DummyRequester requester = new DummyRequester();
		XRemotingInvocationHandler handler = new XRemotingInvocationHandler(new Serializer() {
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
				throw new IOException();
			}
			
			@SuppressWarnings("unchecked")
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				return (T) new Object();
			}
		}, requester);
		
		CoolService target = new CoolService();
		Method method = target.getClass().getMethod("throwing", new Class<?>[]{});
		try {
			handler.invoke(target, method, new Object[]{});
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(InvokingSideInvocationException.class, e.getClass());
		}
		
		requester.assertRequestsAreReleased();
	}
	
	@Test
	public void testSerializationExceptionInSerialize() throws Throwable {
		DummyRequester requester = new DummyRequester();
		XRemotingInvocationHandler handler = new XRemotingInvocationHandler(new Serializer() {
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
				throw new SerializationException();
			}
			
			@SuppressWarnings("unchecked")
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				return (T) new Object();
			}
		}, requester);
		
		CoolService target = new CoolService();
		Method method = target.getClass().getMethod("throwing", new Class<?>[]{});
		try {
			handler.invoke(target, method, new Object[]{});
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(InvokingSideInvocationException.class, e.getClass());
		}
		
		requester.assertRequestsAreReleased();
	}
	
	@Test
	public void testIOExceptionInDeserialize() throws Throwable {
		DummyRequester requester = new DummyRequester();
		XRemotingInvocationHandler handler = new XRemotingInvocationHandler(new Serializer() {
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
			}
			
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				throw new IOException();
			}
		}, requester);
		
		CoolService target = new CoolService();
		Method method = target.getClass().getMethod("throwing", new Class<?>[]{});
		try {
			handler.invoke(target, method, new Object[]{});
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(InvokingSideInvocationException.class, e.getClass());
		}
		
		requester.assertRequestsAreReleased();
	}
	
	@Test
	public void testSerializationExceptionInDeserialize() throws Throwable {
		DummyRequester requester = new DummyRequester();
		XRemotingInvocationHandler handler = new XRemotingInvocationHandler(new Serializer() {
			public void serialize(Object object, OutputStream os)
					throws SerializationException, IOException {
			}
			
			public <T> T deserialize(InputStream is) throws SerializationException,
					IOException {
				throw new SerializationException();
			}
		}, requester);
		
		CoolService target = new CoolService();
		Method method = target.getClass().getMethod("throwing", new Class<?>[]{});
		try {
			handler.invoke(target, method, new Object[]{});
			Assert.fail();
		} catch (Exception e) {
			Assert.assertEquals(InvokingSideInvocationException.class, e.getClass());
		}
		
		requester.assertRequestsAreReleased();
	}
	
}
