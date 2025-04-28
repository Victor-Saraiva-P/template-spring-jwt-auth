package com.victorsaraiva.auth_base_jwt;

import org.junit.jupiter.api.Test;

/**
 * Sobe o contexto Spring completo usando o container PostgreSQL configurado em {@link AbstractIT}.
 * Falha se qualquer bean ou migration der problema.
 */
class AuthBaseJwtApplicationTests extends AbstractIT {

  @Test
  void contextLoads() {
    // não precisa de nada aqui – se chegou até este ponto, o contexto subiu.
  }
}
