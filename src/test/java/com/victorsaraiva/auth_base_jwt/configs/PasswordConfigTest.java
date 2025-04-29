package com.victorsaraiva.auth_base_jwt.configs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTest {

  private final PasswordConfig config = new PasswordConfig();

  @Test
  void passwordEncoderBeanIsNotNullAndOfCorrectType() {
    // Arrange
    // A configuração já está pronta com a instância de config

    // Act
    PasswordEncoder encoder = config.passwordEncoder();

    // Assert
    assertNotNull(encoder, "O PasswordEncoder não deve ser nulo");
    assertInstanceOf(
        BCryptPasswordEncoder.class, encoder, "Deve ser uma instância de BCryptPasswordEncoder");
  }

  @Test
  void bcryptEncoderShouldMatchRawPassword() {
    // Arrange
    PasswordEncoder encoder = config.passwordEncoder();
    String raw = "senha123";

    // Act
    String encoded = encoder.encode(raw);
    boolean matches = encoder.matches(raw, encoded);

    // Assert
    assertNotEquals(raw, encoded, "O raw e o encoded não podem ser iguais");
    assertTrue(
        matches, "O encoder deve validar corretamente a correspondência entre raw e encoded");
  }
}
