package com.example.protobufrockset;

import com.tylrd.model.Person;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Configuration
  static class WebConfig {

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
      return new ProtobufHttpMessageConverter();
    }
  }

  @RestController
  static class PersonController {

    @PostMapping(value = "/", produces = "application/json")
    ResponseEntity<Person> createPerson(@RequestBody Person person) {
      Person p = Person.newBuilder().setName(person.getName()).build();
      return ResponseEntity.ok(p);
    }
  }
}
