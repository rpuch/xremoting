package com.googlecode.xremoting.core.invoked;

/**
 * Restriction on invocation. Currently it only contains a list of exposed
 * interfaces; any invocation of a method which is not defined by one of these
 * interfaces (or their superinterfaces) is rejected.
 * 
 * @author Roman Puchkovskiy
 */
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

	/**
	 * Returns an array of exposed interfaces.
	 * 
	 * @return interfaces
	 */
	public Class<?>[] getInterfaces() {
		return interfaces;
	}

	/**
	 * Sets an array of exposed interfaces.
	 * 
	 * @param interfaces	interfaces to set
	 */
	public void setInterfaces(Class<?>[] interfaces) {
		this.interfaces = interfaces;
	}
}
