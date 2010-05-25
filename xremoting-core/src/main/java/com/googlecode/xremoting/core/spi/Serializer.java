package com.googlecode.xremoting.core.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.xremoting.core.xstream.XStreamSerializer;

/**
 * <p>
 * Central interface for message serialization/deserialization. Knows how to
 * serialize an object to a stream, and how to get an object from a stream.
 * </p>
 * <p>
 * To add support for custom serialization mechanism, implement this interface
 * and then use its instance on client and server sides.
 * </p>
 * 
 * @author Roman Puchkovskiy
 * @see XStreamSerializer
 */
public interface Serializer {
	/**
	 * Serializes an object to an {@link OutputStream}.
	 * 
	 * @param object	object to serialize
	 * @param os		output stream to which to serialize
	 * @throws SerializationException	if something specific to serializer
	 * happened during the serialization
	 * @throws IOException				if input/output error happened
	 */
	void serialize(Object object, OutputStream os) throws SerializationException, IOException;
	
	/**
	 * Deserializes an object an {@link InputStream}.
	 * 
	 * @param <T>	returned type
	 * @param is	input stream from which to deserialize
	 * @return deserialized object
	 * @throws SerializationException	if something specific to serializer
	 * happened during the serialization
	 * @throws IOException				if input/output error happened
	 */
	<T> T deserialize(InputStream is) throws SerializationException, IOException;
}
