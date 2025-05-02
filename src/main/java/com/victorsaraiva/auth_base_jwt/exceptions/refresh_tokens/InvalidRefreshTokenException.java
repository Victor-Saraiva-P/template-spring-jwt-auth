package com.victorsaraiva.auth_base_jwt.exceptions.refresh_tokens;

public class InvalidRefreshTokenException extends RuntimeException {
  public InvalidRefreshTokenException(String refreshToken) {
    super("Refresh token inválido: " + refreshToken);
  }
}
