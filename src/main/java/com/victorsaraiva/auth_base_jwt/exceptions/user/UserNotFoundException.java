package com.victorsaraiva.auth_base_jwt.exceptions.user;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(UUID userId) {
    super("Usuario com id " + userId + " não encontrado");
  }
}
