package com.googlecode.xremoting.core.message;

import java.io.Serializable;

public class Result implements Serializable {
	private static final long serialVersionUID = -4441846621215525525L;
	
	private Object object;

	public Result(Object object) {
		super();
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
