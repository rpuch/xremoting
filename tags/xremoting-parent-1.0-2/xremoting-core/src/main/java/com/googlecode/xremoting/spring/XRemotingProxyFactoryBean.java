package com.googlecode.xremoting.spring;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.caucho.BurlapProxyFactoryBean;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 * Factory bean that produces a proxy for using XRemoting to make remote calls.
 * 
 * @author Roman Puchkovskiy
 * @see HttpInvokerProxyFactoryBean
 * @see HessianProxyFactoryBean
 * @see BurlapProxyFactoryBean
 */
public class XRemotingProxyFactoryBean extends XRemotingClientInterceptor
		implements FactoryBean {

	private Object serviceProxy;

	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		this.serviceProxy = ProxyFactory.getProxy(getServiceInterface(), this);
	}
	
	public Object getObject() {
		return this.serviceProxy;
	}

	public Class<?> getObjectType() {
		return getServiceInterface();
	}
	
	public boolean isSingleton() {
		return true;
	}

}
