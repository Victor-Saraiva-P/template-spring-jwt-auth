package com.victorsaraiva.auth_base_jwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class AuthBaseJwtApplicationTests {

  @MockitoBean private SpringApplication springApplication;

  @Test
  void contextLoads() {
    // Este metodo verifica se o contexto da aplicação Spring Boot é carregado sem erros
  }

  @Test
  void testMainMethod() {
    AuthBaseJwtApplication.main(new String[] {});
  }
}
