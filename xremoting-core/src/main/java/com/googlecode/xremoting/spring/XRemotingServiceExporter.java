package com.googlecode.xremoting.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.web.HttpRequestHandler;

/**
 * Service exporter analogous to {@link HttpInvokerServiceExporter} which
 * exports XRemoting-based service via HTTP(s).
 * 
 * @author Roman Puchkovskiy
 */
public class XRemotingServiceExporter extends XRemotingExporter implements HttpRequestHandler {
	
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = request.getInputStream();
			os = response.getOutputStream();
			invoke(is, os);
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

}
