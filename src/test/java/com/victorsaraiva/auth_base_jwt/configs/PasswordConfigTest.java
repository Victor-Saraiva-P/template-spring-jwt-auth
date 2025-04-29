package com.victorsaraiva.auth_base_jwt.configs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordConfigTest {

  @Autowired private PasswordEncoder passwordEncoder;

  @Test
  public void testPasswordEncoder() {
    // Arrange
    String senhaOriginal = "minhasenha123";

    // Act
    String senhaEncodada = passwordEncoder.encode(senhaOriginal);

    // Assert
    assertNotNull(senhaEncodada);
    assertNotEquals(senhaOriginal, senhaEncodada);
    assertTrue(passwordEncoder.matches(senhaOriginal, senhaEncodada));
  }

  @Test
  public void testSenhasIguaisComEncodingsDiferentes() {
    // Arrange
    String senha = "minhasenha123";

    // Act
    String primeiroEncoding = passwordEncoder.encode(senha);
    String segundoEncoding = passwordEncoder.encode(senha);

    // Assert
    assertNotEquals(primeiroEncoding, segundoEncoding);
    assertTrue(passwordEncoder.matches(senha, primeiroEncoding));
    assertTrue(passwordEncoder.matches(senha, segundoEncoding));
  }
}
