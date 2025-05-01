package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException() {
    super("Credenciais inválidas");
  }
}
