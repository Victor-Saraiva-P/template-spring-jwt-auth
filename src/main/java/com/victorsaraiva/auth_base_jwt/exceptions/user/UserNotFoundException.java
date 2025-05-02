package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String email) {
    super("Usuário com email '" + email + "' não encontrado");
  }
}
