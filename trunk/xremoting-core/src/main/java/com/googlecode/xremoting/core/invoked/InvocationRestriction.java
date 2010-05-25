package com.googlecode.xremoting.core.invoked;

public class InvocationRestriction {
	private Class<?>[] interfaces;
	
	public InvocationRestriction() {
	}
	
	public InvocationRestriction(Class<?> iface) {
		this(new Class<?>[]{iface});
	}
	
	public InvocationRestriction(Class<?>[] interfaces) {
		super();
		this.interfaces = interfaces;
	}

	public Class<?>[] getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(Class<?>[] interfaces) {
		this.interfaces = interfaces;
	}
}
