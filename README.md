# Rockset / Protobuf Demo

This repo is used to reproduce an issue with the Rockset SDK. It stands up a
small a [Spring Boot](https://spring.io/projects/spring-boot) application built
with [Gradle](https://gradle.org/). It uses [Protocol Buffers](https://developers.google.com/protocol-buffers) to generate models that
are returned as JSON payloads using Spring's [ProtobufHttpMessageConverter](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/converter/protobuf/ProtobufHttpMessageConverter.html) class.

## Issue 

In [GSON 2.8.6](https://github.com/google/gson/blob/master/CHANGELOG.md#version-286), `JsonParser.parseString` and `JsonParser.parseReader` were added, and `JsonParser.parse` was deprecated.

In [this commit to Protocol Buffers](https://github.com/protocolbuffers/protobuf/commit/380e352b0bddd31d08250295ef6c24347ae47b54#diff-3873ab618032a676f1ada2e993e3d7621556f7b85b79b5f4fb9439a76876cb44) the `JsonFormat` class was updated to use the new method `JsonParser.parseReader` introduced in GSON 2.8.6. This was released in protobuffers-java 3.18.0 on 2021-09-13.

Rockset Java SDK bundles the version of GSON into the fat jar. Services that use
the SDK, versions > 3.18 of protocol buffers, and Spring Boot could experience `java.lang.NoSuchMethodError`
for `JsonParser.parseReader` when `ProtobufHttpMessageConverter` attempts to convert protobuf messages into JSON, and vice versa.

The [current version GSON in the Rockset SDK is 2.8.1](https://github.com/rockset/rockset-java-client/blob/master/pom.xml#L365)


## Setup

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
this branch is pulling in the rockset SDK.

Run the tests again to see the error:

```
$ ./gradlew test --info

> Task :test

DemoApplicationTests > shouldReturnPerson() FAILED
    org.springframework.web.util.NestedServletException at DemoApplicationTests.java:21
        Caused by: java.lang.NoSuchMethodError at DemoApplicationTests.java:21
		
DemoApplicationTests > shouldReturnPerson() FAILED
    org.springframework.web.util.NestedServletException: Handler dispatch failed; nested exception is java.lang.NoSuchMethodError: 'com.google.gson.JsonElement com.google.gson.JsonParser.parseReader(com.google.gson.stream.JsonReader)'
		....

        Caused by:
        java.lang.NoSuchMethodError: 'com.google.gson.JsonElement com.google.gson.JsonParser.parseReader(com.google.gson.stream.JsonReader)'
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
            ... 20 more
```

This implies that the
`com.google.gson.JsonParser.parseReader(com.google.gson.stream.JsonReader)`
method signature does not exist in the com.google.gson:gson library that is
being resolved by the build.

The all files in the fat jar generated with `./gradlew assemble`:

```
META-INF/
META-INF/MANIFEST.MF
BOOT-INF/
BOOT-INF/classes/
BOOT-INF/classes/com/
BOOT-INF/classes/com/tylrd/
BOOT-INF/classes/com/tylrd/DemoApplication.class
BOOT-INF/classes/com/tylrd/model/
BOOT-INF/classes/com/tylrd/model/Person.class
BOOT-INF/classes/com/tylrd/model/Person$1.class
BOOT-INF/classes/com/tylrd/model/Models.class
BOOT-INF/classes/com/tylrd/model/Person$Builder.class
BOOT-INF/classes/com/tylrd/model/PersonOrBuilder.class
BOOT-INF/classes/com/tylrd/DemoApplication$PersonController.class
BOOT-INF/classes/com/tylrd/DemoApplication$WebConfig.class
BOOT-INF/classes/com/tylrd/model/Person.proto
BOOT-INF/classes/application.properties
BOOT-INF/lib/
BOOT-INF/lib/protobuf-java-util-3.19.2.jar
BOOT-INF/lib/protobuf-java-3.19.2.jar
BOOT-INF/lib/rockset-java-0.9.1.jar
BOOT-INF/lib/spring-webmvc-5.3.18.jar
BOOT-INF/lib/spring-web-5.3.18.jar
BOOT-INF/lib/guava-30.1.1-android.jar
BOOT-INF/lib/error_prone_annotations-2.5.1.jar
BOOT-INF/lib/j2objc-annotations-1.3.jar
BOOT-INF/lib/jsr305-3.0.2.jar
BOOT-INF/lib/gson-2.8.9.jar
BOOT-INF/lib/spring-boot-autoconfigure-2.6.6.jar
BOOT-INF/lib/spring-boot-2.6.6.jar
BOOT-INF/lib/jakarta.annotation-api-1.3.5.jar
BOOT-INF/lib/spring-context-5.3.18.jar
BOOT-INF/lib/spring-expression-5.3.18.jar
BOOT-INF/lib/spring-aop-5.3.18.jar
BOOT-INF/lib/spring-beans-5.3.18.jar
BOOT-INF/lib/spring-core-5.3.18.jar
BOOT-INF/lib/snakeyaml-1.29.jar
BOOT-INF/lib/jackson-datatype-jsr310-2.13.2.jar
BOOT-INF/lib/jackson-module-parameter-names-2.13.2.jar
BOOT-INF/lib/jackson-annotations-2.13.2.jar
BOOT-INF/lib/jackson-core-2.13.2.jar
BOOT-INF/lib/jackson-datatype-jdk8-2.13.2.jar
BOOT-INF/lib/jackson-databind-2.13.2.2.jar
BOOT-INF/lib/tomcat-embed-websocket-9.0.60.jar
BOOT-INF/lib/tomcat-embed-core-9.0.60.jar
BOOT-INF/lib/tomcat-embed-el-9.0.60.jar
BOOT-INF/lib/failureaccess-1.0.1.jar
BOOT-INF/lib/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar
BOOT-INF/lib/checker-compat-qual-2.5.5.jar
BOOT-INF/lib/logback-classic-1.2.11.jar
BOOT-INF/lib/log4j-to-slf4j-2.17.2.jar
BOOT-INF/lib/jul-to-slf4j-1.7.36.jar
BOOT-INF/lib/spring-jcl-5.3.18.jar
BOOT-INF/lib/logback-core-1.2.11.jar
BOOT-INF/lib/slf4j-api-1.7.36.jar
BOOT-INF/lib/log4j-api-2.17.2.jar
BOOT-INF/classes/static/
BOOT-INF/classes/templates/
BOOT-INF/lib/spring-boot-jarmode-layertools-2.6.6.jar
BOOT-INF/classpath.idx
BOOT-INF/layers.idx
```

## Potential Solutions

1. Upgrade rockset-java SDK to latest GSON

Update the GSON version from 2.8.1 to 2.8.9 [here](https://github.com/rockset/rockset-java-client/blob/master/pom.xml#L365)
This would fix the immediate issue, but might not "future proof" the SDK from
similar dependency issues.

2. Add class relocation to the fat jar

[Class Relocation](https://maven.apache.org/plugins/maven-shade-plugin/examples/class-relocation.html) is a technique that relocates the classes which get included in the shaded artifact in order to create a private copy of their bytecode.

The `maven-shade-plugin` supports shading dependencies as part of its
coniguration.

```diff
diff --git a/pom.xml b/pom.xml
index b3b45b9..1eefd2f 100644
--- a/pom.xml
+++ b/pom.xml
@@ -76,6 +76,12 @@
                     </execution>
                 </executions>
                 <configuration>
+                    <relocations>
+                        <relocation>
+                            <pattern>com.google</pattern>
+                            <shadedPattern>com.shaded.google</shadedPattern>
+                        </relocation>
+                    </relocations>
                     <filters>
                         <filter>
                             <artifact>*:*</artifact>
```

This patch was tested locally and fixed the issue.
