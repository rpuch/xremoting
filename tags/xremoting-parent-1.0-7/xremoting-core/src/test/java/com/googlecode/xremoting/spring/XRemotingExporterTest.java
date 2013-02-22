package com.googlecode.xremoting.spring;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.xremoting.core.test.CoolService;
import com.googlecode.xremoting.core.test.CoolServiceInterface;
import com.googlecode.xremoting.core.xstream.XStreamSerializer;

public class XRemotingExporterTest {
	@Test
	public void testInitDefaultSerializer() throws Exception {
		XRemotingExporter exporter = new XRemotingExporter();

		exporter.setServiceInterface(CoolServiceInterface.class);
		exporter.setService(new CoolService());
		
		exporter.afterPropertiesSet();
	}
	
	@Test
	public void testInitSerializer() throws Exception {
		XRemotingExporter exporter = new XRemotingExporter();

		exporter.setServiceInterface(CoolServiceInterface.class);
		exporter.setService(new CoolService());
		exporter.setSerializer(new XStreamSerializer());
		
		exporter.afterPropertiesSet();
	}
	
	@Test
	public void testInitSerializerClass() throws Exception {
		XRemotingExporter exporter = new XRemotingExporter();

		exporter.setServiceInterface(CoolServiceInterface.class);
		exporter.setService(new CoolService());
		exporter.setSerializerClass(XStreamSerializer.class);
		
		exporter.afterPropertiesSet();
	}
	
	@Test
	public void testInitSerializerClassName() throws Exception {
		XRemotingExporter exporter;
		exporter = new XRemotingExporter();

		exporter.setServiceInterface(CoolServiceInterface.class);
		exporter.setService(new CoolService());
		exporter.setSerializerClassName(XStreamSerializer.class.getName());
		
		exporter.afterPropertiesSet();
		
		exporter = new XRemotingExporter();

		exporter.setServiceInterface(CoolServiceInterface.class);
		exporter.setService(new CoolService());
		exporter.setSerializerClassName(XStreamSerializer.class.getName());
		exporter.setSerializerClassLoader(new ClassLoader(getClass().getClassLoader()) {
		});
		
		exporter.afterPropertiesSet();
	}
	
	@Test
	public void testInitWrong() throws Exception {
		XRemotingExporter exporter;
		exporter = new XRemotingExporter();

		exporter.setServiceInterface(CoolServiceInterface.class);
		exporter.setService(new CoolService());
		exporter.setSerializer(new XStreamSerializer());
		exporter.setSerializerClass(XStreamSerializer.class);

		try {
			exporter.afterPropertiesSet();
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
		
		exporter = new XRemotingExporter();

		exporter.setServiceInterface(CoolServiceInterface.class);
		exporter.setService(new CoolService());
		exporter.setSerializer(new XStreamSerializer());
		exporter.setSerializerClassName(XStreamSerializer.class.getName());

		try {
			exporter.afterPropertiesSet();
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
		
		exporter = new XRemotingExporter();

		exporter.setServiceInterface(CoolServiceInterface.class);
		exporter.setService(new CoolService());
		exporter.setSerializerClass(XStreamSerializer.class);
		exporter.setSerializerClassName(XStreamSerializer.class.getName());

		try {
			exporter.afterPropertiesSet();
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
}
