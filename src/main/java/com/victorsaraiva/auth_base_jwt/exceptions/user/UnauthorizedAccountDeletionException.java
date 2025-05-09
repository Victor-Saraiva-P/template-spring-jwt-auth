package com.victorsaraiva.auth_base_jwt.exceptions.user;

public class UnauthorizedAccountDeletionException extends RuntimeException {
  public UnauthorizedAccountDeletionException() {
    super("Você só pode deletar sua própria conta");
  }
}
