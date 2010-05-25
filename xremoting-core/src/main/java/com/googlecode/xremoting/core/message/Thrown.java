package com.googlecode.xremoting.core.message;

import java.io.Serializable;

public class Thrown implements Serializable {
	private static final long serialVersionUID = 7653345554991408137L;
	
	private Throwable throwable;

	public Thrown(Throwable throwable) {
		super();
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

}
