package com.example.protobufrockset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnPerson() throws Exception {
    this.mockMvc
        .perform(post("/").contentType("application/json").content("{\"name\": \"Taylor\"}"))
        .andExpect(status().isOk());
  }
}
