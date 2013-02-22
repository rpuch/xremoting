package com.googlecode.xremoting.core.utils;

import org.junit.Assert;
import org.junit.Test;

public class ClassLoaderUtilsTest {
	@Test
	public void testGetDefaultClassLoader() throws Exception {
		ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader cl;
		try {
			Thread.currentThread().setContextClassLoader(null);
			cl = ClassLoaderUtils.getDefaultClassLoader(getClass());
			Assert.assertSame(getClass().getClassLoader(), cl);
			
			ClassLoader anotherClassLoader = new ClassLoader() {};
			Thread.currentThread().setContextClassLoader(anotherClassLoader);
			cl = ClassLoaderUtils.getDefaultClassLoader(getClass());
			Assert.assertSame(anotherClassLoader, cl);
			Assert.assertNotSame(getClass().getClassLoader(), anotherClassLoader);
		} finally {
			Thread.currentThread().setContextClassLoader(originalClassLoader);
		}
	}
}
