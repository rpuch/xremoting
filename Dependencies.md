# Java version #

XRemoting requires Java 5 for building and running.

# Libraries #

To use XRemoting at runtime, you will need the following libraries:

  * xremoting-core-${version}.jar
  * xstream-1.1.1.jar (or higher, only if you need XStream serialization which is default)
  * xpp3.jar (required by XStream)
  * commons-httpclient-3.0.jar (or higher, only, if you need httpclient)

## Using maven2 ##

Here's an example for project which uses both XStream for serialization and httpclient for transport:

```
<repositories>
    <repository>
        <id>xremoting-release</id>
        <name>xremoting release</name>
        <url>http://xremoting.googlecode.com/svn/maven2/release</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.googlecode.xremoting</groupId>
        <artifactId>xremoting-core</artifactId>
        <version>1.0-3</version>
    </dependency>
    <dependency>
        <groupId>com.thoughtworks.xstream</groupId>
        <artifactId>xstream</artifactId>
        <version>1.1.1</version>
    </dependency>
    <dependency>
        <groupId>commons-httpclient</groupId>
        <artifactId>commons-httpclient</artifactId>
        <version>3.1</version>
    </dependency>
</dependencies>
```