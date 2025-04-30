package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class UserOperationException extends RuntimeException {
  public UserOperationException(String message, Throwable cause) {
    super(message, cause);
  }
}
