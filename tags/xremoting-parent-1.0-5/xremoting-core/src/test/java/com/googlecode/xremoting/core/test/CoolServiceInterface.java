package com.googlecode.xremoting.core.test;

public interface CoolServiceInterface {
	String doCoolStuff(String stringArg, Integer integerArg, int intArg, Class<?>[] classArrayArg);
	
	void throwing() throws Exception;
	
	Person justInvoke(String abc, Object[] objects);
}
