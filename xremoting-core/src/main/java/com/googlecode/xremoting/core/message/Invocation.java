package com.googlecode.xremoting.core.message;

import java.io.Serializable;

/**
 * Value object for invocation.
 * 
 * @author Roman Puchkovskiy
 */
public class Invocation implements Serializable {
	private static final long serialVersionUID = -4475087657362943906L;
	
	private String methodName;
	private Class<? extends Object>[] argTypes;
	private Object[] args;

	public Invocation(String methodName, Class<? extends Object>[] argTypes, Object[] args) {
		super();
		this.methodName = methodName;
		this.argTypes = argTypes;
		this.args = args;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<? extends Object>[] getArgTypes() {
		return argTypes;
	}

	public void setArgTypes(Class<? extends Object>[] argTypes) {
		this.argTypes = argTypes;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

}
