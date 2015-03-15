# XRemoting #

## What is it? ##

Remoting is used to make calls from one machine to another one. There are lot of options for remoting, and there's a lot of good software for this, but some of these libraries are way too complex, others bind you too much to some proprietary protocols.

This framework was created to satisfy the following requirements:

  * Simple as simple it can be in a simple case (using HTTP + XStream)
  * Extensible: architecture should allow you to build a space rocket, if you would like to :)
  * Not only simple for a developer, but also simple for a deployer (requires a minimum of libraries)
  * Embeddability
  * Support for HTTPS (including client authentication via SSL) out of the box
  * [Spring](http://springframework.org) integration (both exposing an service on server side and factory bean for calling side like [HttpInvokerProxyFactoryBean](http://static.springsource.org/spring/docs/2.0.x/api/org/springframework/remoting/httpinvoker/HttpInvokerProxyFactoryBean.html) or [HessianProxyFactoryBean](http://static.springsource.org/spring/docs/2.0.x/api/org/springframework/remoting/caucho/HessianProxyFactoryBean.html))

## Simple example ##

### Server side ###

```
public class YourServiceServlet extends XRemotingServlet {
    private YouService yourService = new YourServiceImpl();
    protected Object getTarget() {
        return yourService;
    }
}
```

In web.xml:

```
<servlet>
    <servlet-name>yourServiceServlet</servlet-name>
    <servlet-class>com.yourpackage.YourServiceServlet</servlet-class>
    <init-param>
        <param-name>exposedInterfaces</param-name>
        <param-value>com.yourpackage.YourService</param-value>
    </init-param>
</servlet>
<servlet-mapping>
    <servlet-name>yourServiceServlet</servlet-name>
    <url-pattern>/your-service</url-pattern>
</servlet-mapping>
```

### Client side ###

```
XRemotingProxyFactory factory = new XRemotingProxyFactory("http://yourhost.com/your-service");
YourService yourService = (YourService) factory.create(YourService.class);
// now call any methods on yourService
```

## Documentation ##

You may wish to look at the following pages:
  * [Introduction](Introduction.md)
  * [Dependencies](Dependencies.md)
  * [Exposing a service via HTTP(s)](ExposingServiceViaHttp.md)
  * [Calling a remote service](CallingRemoteService.md)
  * [Spring integration](SpringIntegration.md)
  * [Extending](Extending.md)
  * [Developing](Developing.md)