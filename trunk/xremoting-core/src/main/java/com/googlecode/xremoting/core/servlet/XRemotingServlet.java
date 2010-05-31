package com.googlecode.xremoting.core.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.xremoting.core.invoked.DefaultInvoker;
import com.googlecode.xremoting.core.invoked.InvocationRestriction;
import com.googlecode.xremoting.core.invoked.Invoker;
import com.googlecode.xremoting.core.invoked.ProxyInvokingHelper;
import com.googlecode.xremoting.core.spi.Serializer;
import com.googlecode.xremoting.core.utils.ClassLoaderUtils;
import com.googlecode.xremoting.core.xstream.XStreamSerializer;

/**
 * Base for a servlet which wishes to expose a service using XRemoting to the
 * world via HTTP.
 * 
 * @author Roman Puchkovskiy
 */
public abstract class XRemotingServlet extends HttpServlet {
	private static final long serialVersionUID = 3625935027430412706L;
	
	private Serializer serializer;
	private Invoker invoker;
	private ProxyInvokingHelper invokingHelper;
	private InvocationRestriction restriction;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		serializer = createSerializer();
		invoker = createInvoker();
		invokingHelper = createInvokingHelper();
		doInit(config);
	}
	
	protected void doInit(ServletConfig config) throws ServletException {
		String interfacesString = config.getInitParameter("exposedInterfaces");
		if (interfacesString == null) {
			throw new ServletException("init-parameter called exposedInterfaces is required");
		}
		ClassLoader classLoader = getInterfacesClassLoader();
		List<Class<?>> interfaces = new ArrayList<Class<?>>();
		StringTokenizer tokenizer = new StringTokenizer(interfacesString, "\t\n\r\f,");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if (token.length() > 0) {
				Class<?> iface;
				try {
					iface = Class.forName(token, true, classLoader);
				} catch (ClassNotFoundException e) {
					throw new ServletException(e);
				}
				interfaces.add(iface);
			}
		}

		restriction = createInvocationRestriction(interfaces.toArray(new Class<?>[interfaces.size()]));
	}

	protected InvocationRestriction createInvocationRestriction(Class<?>[] interfaces) {
		return new InvocationRestriction(interfaces);
	}

	protected ClassLoader getInterfacesClassLoader() {
		return ClassLoaderUtils.getDefaultClassLoader(getClass());
	}

	protected Serializer createSerializer() {
		return new XStreamSerializer();
	}
	
	protected Invoker createInvoker() {
		return new DefaultInvoker();
	}
	
	protected ProxyInvokingHelper createInvokingHelper() {
		return new ProxyInvokingHelper();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = request.getInputStream();
			os = response.getOutputStream();
			beforeInvocation(request, response);
			try {
				invokingHelper.invoke(getTarget(), is, os, serializer, invoker, restriction);
			} finally {
				afterInvocation(request, response);
			}
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} finally {
				if (os != null) {
					os.close();
				}
			}
		}
	}
	
	protected void beforeInvocation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	protected void afterInvocation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	protected abstract Object getTarget();

}
