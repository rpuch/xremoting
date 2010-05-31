package com.googlecode.xremoting.core.xstream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.message.Result;
import com.googlecode.xremoting.core.message.Thrown;
import com.googlecode.xremoting.core.test.CoolService;
import com.googlecode.xremoting.core.test.Person;

public class XStreamSerializerTest {
	private XStreamSerializer serializer = new XStreamSerializer();
	
	@Test
	public void testSimple() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Person person = new Person();
		person.setFirstName("Kashey");
		person.setLastName("Bessmertnyi");
		person.setAge(999);
		serializer.serialize(person, baos);
		
		Person person2 = serializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
		Assert.assertEquals(person.getFirstName(), person2.getFirstName());
		Assert.assertEquals(person.getLastName(), person2.getLastName());
		Assert.assertEquals(person.getAge(), person2.getAge());
		Assert.assertEquals(person.getHobby(), person2.getHobby());
		
		Element root = getRootElement(baos);
		Assert.assertEquals(Person.class.getName(), root.getNodeName());
	}
	
	@Test
	public void testNonAscii() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Person person = new Person();
		person.setFirstName("Кащей");
		person.setLastName("Бессмертный");
		person.setAge(999);
		serializer.serialize(person, baos);
		
		Person person2 = serializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
		Assert.assertEquals(person.getFirstName(), person2.getFirstName());
		Assert.assertEquals(person.getLastName(), person2.getLastName());
		Assert.assertEquals(person.getAge(), person2.getAge());
		Assert.assertEquals(person.getHobby(), person2.getHobby());
		
		Element root = getRootElement(baos);
		Assert.assertEquals(Person.class.getName(), root.getNodeName());
	}

	private Element getRootElement(ByteArrayOutputStream baos)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new ByteArrayInputStream(baos.toByteArray()));
		Element root = document.getDocumentElement();
		return root;
	}
	
	@Test
	public void testInvocation() throws Exception {
		Method method = CoolService.class.getDeclaredMethods()[0];
		Invocation invocation = new Invocation(method.getName(), method.getParameterTypes(),
				new Object[]{"some string", 15, 166, new Class[]{Object.class, String.class}});
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(invocation, baos);
		
		Invocation invocation2 = serializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
		Assert.assertEquals(invocation.getMethodName(), invocation2.getMethodName());
		Assert.assertArrayEquals(invocation.getArgTypes(), invocation2.getArgTypes());
		Assert.assertArrayEquals(invocation.getArgs(), invocation2.getArgs());
		
		Element root = getRootElement(baos);
		Assert.assertEquals("invocation", root.getNodeName());
	}
	
	@Test
	public void testResult() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Result result = new Result("ha-ha!");
		serializer.serialize(result, baos);
		
		Result result2 = serializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
		Assert.assertEquals(result.getObject(), result2.getObject());
		
		Element root = getRootElement(baos);
		Assert.assertEquals("result", root.getNodeName());
	}
	
	@Test
	public void testThrown() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Thrown thrown = new Thrown(new Exception("I'm exception"));
		serializer.serialize(thrown, baos);
		
		Thrown thrown2 = serializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
		Assert.assertEquals(thrown.getThrowable().getMessage(), thrown2.getThrowable().getMessage());
		
		Element root = getRootElement(baos);
		Assert.assertEquals("thrown", root.getNodeName());
	}
	
}
