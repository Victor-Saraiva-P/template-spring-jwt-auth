package com.victorsaraiva.auth_base_jwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthBaseJwtApplicationTests {

  @Test
  void contextLoads() {
    // Este metodo está vazio propositalmente, pois verifica apenas
    // se o contexto da aplicação Spring Boot é carregado sem erros
  }
}
