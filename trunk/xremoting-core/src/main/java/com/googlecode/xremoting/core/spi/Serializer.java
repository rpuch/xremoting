package com.googlecode.xremoting.core.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public interface Serializer {
	void serialize(Object object, OutputStream os) throws SerializationException, IOException;
	<T> T deserialize(InputStream is) throws SerializationException, IOException;
}
