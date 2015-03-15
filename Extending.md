# Introduction #

One of XRemoting features is extensibility. Currently, there're two 'extension points':

  * Serializer
  * Requester

# Serializer #

Serializer is an interface which responsibility is to serialize a value object to a stream and to deserialize such a value from a stream.

To use your own implementation of Serializer, you need to do the following:

  * write Serializer implementation which suites your needs (both serialize() and deserialize() methods)
  * use this Serializer implementation on both sides: on the server side (exposing side) and on the client side (invoking side)

# Requester #

Requester's responsibility is to make a request and return its result. It's just a factory for Request instances, your will probably have to implement Request too.

Standard XRemoting SPI interfaces are stream-oriented (they are designed to use InputStream and OutputStream for transport). But you may write a couple of Serializer and Requester meant to work together which do not use streams at all but instead use some other technique.

When writing your own Requester, you will have to use it on the client side, of course. But will you have to change your server-side logic or not, it depends on your Requester logic. For instance, if it uses standard HTTP(s) to make requests, you will not need any modifications on server side.

# Other ways to expose a service #

XRemoting includes two ways to expose an interface: through a servlet of through Spring's HttpRequestHandler machinery. You may use them as a reference implementations to implement your own exposers (filters or something else, like protocol which is not HTTP).