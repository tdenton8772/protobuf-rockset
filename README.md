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

