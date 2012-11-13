package com.googlecode.xremoting.core.xstream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.message.Result;
import com.googlecode.xremoting.core.message.Thrown;
import com.googlecode.xremoting.core.spi.SerializationException;
import com.googlecode.xremoting.core.spi.Serializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.BaseException;
import com.thoughtworks.xstream.io.StreamException;

/**
 * <p>
 * {@link Serializer} which uses XStream as a serialization engine. So,
 * basically, data is converted to XML during the serialization.
 * </p>
 * <p>
 * This is the default XRemoting serializer.
 * </p>
 * 
 * @author Roman Puchkovskiy
 */
public class XStreamSerializer implements Serializer {
	
	protected XStream xstream;
	
	public XStreamSerializer() {
		super();
		xstream = createXStream();
	}
	
	protected XStream createXStream() {
		XStream xs = new XStream();
		xs.alias("invocation", Invocation.class);
		xs.alias("result", Result.class);
		xs.alias("thrown", Thrown.class);
		return xs;
	}

	public void serialize(Object object, OutputStream os) throws SerializationException, IOException {
		try {
			xstream.toXML(object, createWriter(os));
		} catch (StreamException e) {
			throw new IOException(e);
		} catch (BaseException e) {
			throw new SerializationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T deserialize(InputStream is) throws SerializationException, IOException {
		try {
			return (T) xstream.fromXML(createReader(is));
		} catch (BaseException e) {
			throw new SerializationException(e);
		}
	}
	
	protected Writer createWriter(OutputStream os) throws UnsupportedEncodingException {
		return new OutputStreamWriter(os, getDefaultCharset());
	}
	
	protected Reader createReader(InputStream is) throws UnsupportedEncodingException {
		return new InputStreamReader(is, getDefaultCharset());
	}
	
	protected String getDefaultCharset() {
		return "utf-8";
	}

}
