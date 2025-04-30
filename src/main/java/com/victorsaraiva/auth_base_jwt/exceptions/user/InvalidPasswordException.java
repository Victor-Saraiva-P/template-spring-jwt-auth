package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class InvalidPasswordException extends RuntimeException {
  public InvalidPasswordException() {
    super("Senha inválida");
  }
}
