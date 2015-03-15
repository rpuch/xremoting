# Introduction #

[Spring framework](http://springframework.org) includes rich support for remoting using different remote protocols (RMI, Hessian, Burlap, etc.). This page describes how to integrate XRemoting with Spring for both exposing a service via HTTP(s) and invoking such a service.

# Exposing a service via HTTP(s) #

In your context XML configuration, define something like the following:

```
<bean id="yourService" class="com.yourpackage.YourServiceImpl"/>

<bean id="yourServiceExporter" class="com.googlecode.xremoting.spring.XRemotingServiceExporter">
    <property name="serviceInterface" value="com.yourpackage.YourService"/>
    <property name="service" ref="yourService"/>
</bean>
```

After this, use one of the standard ways of exposing a service exporter.

## Defining in a UrlMapping for a DispatcherServlet ##

```
<bean id="urlRemoteMapping" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
    <property name="mappings">
        <value>
            /your-service=yourServiceExporter
        </value>
    </property>
</bean>
```

## Exposing through an HttpRequestHandlerServlet ##

```
<servlet>
    <servlet-name>accountExporter</servlet-name>
    <servlet-class>org.springframework.web.context.support.HttpRequestHandlerServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>yourServiceExporter</servlet-name>
    <url-pattern>/your-service</url-pattern>
</servlet-mapping>
```

# Invoking a service via HTTP(s) #

Here's what you may define in your client-side Spring context:

```
<bean id="yourService" class="com.googlecode.xremoting.spring.XRemotingProxyFactoryBean">
    <property name="serviceUrl" value="http://yourdomain.com/cool-service"/>
    <property name="serviceInterface" value="com.yourpackage.YourService"/>
</bean>
```

Then just use factoryBean.getBean("yourService") or standard Dependency Injection techniques.

!XRemotingProxyFactoryBean has a lot of properties which allow to enable SSL (including client auth), BASIC authentication, HTTP proxy, or even use your own Serializer or Requester implementation. See javadocs for details.