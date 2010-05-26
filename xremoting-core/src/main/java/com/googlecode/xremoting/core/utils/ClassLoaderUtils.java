package com.googlecode.xremoting.core.utils;

/**
 * Utilities which help working with classloaders.
 * 
 * @author Roman Puchkovskiy
 */
public class ClassLoaderUtils {
	/**
	 * Determines the 'default' classloader. That is context class loader, if
	 * defined, or else classloader which loaded the given class.
	 * 
	 * @param clazz		to which classloader to fall back
	 * @return classloader
	 */
	public static ClassLoader getDefaultClassLoader(Class<?> clazz) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = clazz.getClassLoader();
		}
		return classLoader;
	}
}
