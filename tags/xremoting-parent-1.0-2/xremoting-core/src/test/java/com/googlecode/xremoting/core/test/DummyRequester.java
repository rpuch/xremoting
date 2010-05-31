/**
 * 
 */
package com.googlecode.xremoting.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;

public class DummyRequester implements Requester {
	private List<DummyRequest> requests = new ArrayList<DummyRequest>();
	
	public Request createRequest() throws IOException {
		DummyRequest request = new DummyRequest();
		requests.add(request);
		return request;
	}
	
	public synchronized void assertRequestsAreReleased() {
		for (DummyRequest request : requests) {
			Assert.assertTrue(request.isReleased());
		}
		requests.clear();
	}
}