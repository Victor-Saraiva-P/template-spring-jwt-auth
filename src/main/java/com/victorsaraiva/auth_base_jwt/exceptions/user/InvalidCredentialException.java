package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class InvalidCredentialException extends RuntimeException {
  public InvalidCredentialException() {
    super("Credenciais inválidas");
  }
}
