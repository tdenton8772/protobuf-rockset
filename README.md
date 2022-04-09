# Rockset / Protobuf Demo

This repo is used to reproduce an issue with the Rockset SDK. It stands up a
small a [Spring Boot](https://spring.io/projects/spring-boot) application built
with [Gradle](https://gradle.org/). It uses [Protocol Buffers](https://developers.google.com/protocol-buffers) to generate models that
are returned as JSON payloads using Spring's [ProtobufHttpMessageConverter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/converter/protobuf/ProtobufHttpMessageConverter.html) class.

## Quickstart

Make sure JDK 11 is installed and `JAVA_HOME` is pointed to a JDK installation.

To run passing build and tests:

```
git checkout main
./gradlew build
```

To run manual tests from the terminal, start the web server:

```
./gradlew bootRun
```

And test with curl:

```
curl -i \
    -X POST \
    -d '{"name": "taylor"}' \
    -H 'Content-Type: application/json' \
    http://localhost:8080/
```

## Reproducible Issue

Checkout out the `rockset-sdk` branch to reproduce the issue. The only change to
this branch is pulling in the rockset SDK:

```

```

Run the tests again to see the error:

```
$ ./gradlew test --info

> Task :test

DemoApplicationTests > shouldReturnPerson() FAILED
    org.springframework.web.util.NestedServletException at DemoApplicationTests.java:21
        Caused by: java.lang.NoSuchMethodError at DemoApplicationTests.java:21
```


```
Caused by: java.lang.NoSuchMethodError: 'com.google.gson.JsonElement com.google.gson.JsonParser.parseReader(com.google.gson.stream.JsonReader)'
	at com.google.protobuf.util.JsonFormat$ParserImpl.merge(JsonFormat.java:1320)
	at com.google.protobuf.util.JsonFormat$Parser.merge(JsonFormat.java:491)
	at org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter$ProtobufJavaUtilSupport.merge(ProtobufHttpMessageConverter.java:396)
	at org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.readInternal(ProtobufHttpMessageConverter.java:202)
	at org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.readInternal(ProtobufHttpMessageConverter.java:86)
	at org.springframework.http.converter.AbstractHttpMessageConverter.read(AbstractHttpMessageConverter.java:199)
	at org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver.readWithMessageConverters(AbstractMessageConverterMethodArgumentResolver.java:186)
	at org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.readWithMessageConverters(RequestResponseBodyMethodProcessor.java:160)
	at org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.resolveArgument(RequestResponseBodyMethodProcessor.java:133)
	at org.springframework.web.method.support.HandlerMethodArgumentResolverComposite.resolveArgument(HandlerMethodArgumentResolverComposite.java:122)
	at org.springframework.web.method.support.InvocableHandlerMethod.getMethodArgumentValues(InvocableHandlerMethod.java:179)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:146)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:117)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:808)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1067)
	... 103 more
```

This implies that the
`com.google.gson.JsonParser.parseReader(com.google.gson.stream.JsonReader)`
method signature does not exist in the com.google.gson:gson library.

Looking at the dependency resolution for this library:

``````
