package com.googlecode.xremoting.core.test;

public class CoolService implements CoolServiceInterface {
	public String doCoolStuff(String stringArg, Integer integerArg, int intArg, Class<?>[] classArrayArg) {
		return stringArg + integerArg + intArg + classArrayArg[0].getSimpleName();
	}
	
	public void throwing() throws Exception {
		throw new Exception("Just thrown");
	}
	
	public Person justInvoke(String abc, Object[] objects) {
		return null;
	}
}
